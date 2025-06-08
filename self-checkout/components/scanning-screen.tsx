"use client"

import { Button } from "@/components/ui/button"
import { Card, CardContent } from "@/components/ui/card"
import type { Product, Coupon } from "@/lib/types"
import { Beer, Award, Scan, Trash2, Barcode } from "lucide-react"
import Image from "next/image"
import { useState, useEffect } from "react"
import SockJS from "sockjs-client"
import { Client } from "@stomp/stompjs"

interface ScanningScreenProps {
  cart: Product[]
  onUpdateQuantity: (productId: number, quantity: number) => void
  onRemoveProduct: (productId: number) => void
  onAddProduct: (product: Product) => void
  onCheckout: () => void
  onCancel: () => void
  onAddBeerClick: () => void
  onMyPointsClick: () => void
  onBarcodeInputClick: () => void
  appliedCoupon: Coupon | null
  pointsToEarn: number
}

interface ProdutoDto {
  ean: string
  nome: string
  urlImagem: string
  valor: number
  produtoMaiorDeIdade: boolean
}

interface ErrorMessage {
  message: string
}

export default function ScanningScreen({
                                         cart,
                                         onUpdateQuantity,
                                         onRemoveProduct,
                                         onAddProduct,
                                         onCheckout,
                                         onCancel,
                                         onAddBeerClick,
                                         onMyPointsClick,
                                         onBarcodeInputClick,
                                         appliedCoupon,
                                         pointsToEarn,
                                       }: ScanningScreenProps) {
  const [stompClient, setStompClient] = useState<Client | null>(null)
  const [notification, setNotification] = useState<string | null>(null)

  // Configurar WebSocket
  useEffect(() => {
    const socket = new SockJS("http://localhost:8080/ws")
    const client = new Client({
      webSocketFactory: () => socket,
      reconnectDelay: 5000,
      onConnect: () => {
        client.subscribe("/topic/scanned-product", (message) => {
          const data = JSON.parse(message.body)
          if ("message" in data) {
            setNotification((data as ErrorMessage).message)
            setTimeout(() => setNotification(null), 3000)
          } else {
            const produtoDto = data as ProdutoDto
            const scannedEan = produtoDto.ean
            const existingProduct = cart.find((item) => item.ean === scannedEan)
            if (existingProduct) {
              onUpdateQuantity(existingProduct.id, existingProduct.quantity + 1)
            } else {
              const product: Product = {
                id: Date.now(),
                name: produtoDto.nome,
                price: produtoDto.valor,
                quantity: 1,
                image: produtoDto.urlImagem || "/placeholder.svg?height=64&width=64",
                ean: scannedEan,
              }
              onAddProduct(product)
            }
          }
        })
      },
      onStompError: (frame) => {
        console.error("Erro STOMP:", frame)
      },
      onWebSocketError: (err) => {
        console.error("Erro WebSocket:", err)
      },
    })

    client.activate()
    setStompClient(client)

    return () => {
      client.deactivate()
    }
  }, [cart, onAddProduct, onUpdateQuantity])

  const calculateSubtotal = () => {
    return cart.reduce((sum, item) => sum + item.price * item.quantity, 0)
  }

  const calculateDiscount = () => {
    if (!appliedCoupon) return 0
    const subtotal = calculateSubtotal()
    if (appliedCoupon.minPurchase && subtotal < appliedCoupon.minPurchase) {
      return 0
    }
    if (appliedCoupon.type === "percentage") {
      const percentageDiscount = subtotal * (appliedCoupon.value / 100)
      if (appliedCoupon.maxDiscount) {
        return Math.min(percentageDiscount, appliedCoupon.maxDiscount)
      }
      return percentageDiscount
    } else {
      return Math.min(appliedCoupon.value, subtotal)
    }
  }

  const calculateTotal = () => {
    return calculateSubtotal() - calculateDiscount()
  }

  const getProductImage = (productId: number) => {
    switch (productId) {
      case 1:
        return "/images/mineral-water.png"
      case 2:
        return "/images/beer.png"
      default:
        return "/placeholder.svg?height=64&width=64"
    }
  }

  const isCouponValid = () => {
    if (!appliedCoupon) return true
    const subtotal = calculateSubtotal()
    return !(appliedCoupon.minPurchase && subtotal < appliedCoupon.minPurchase)
  }

  const getCouponStatusMessage = () => {
    if (!appliedCoupon || isCouponValid()) return null
    return `Para aplicar este cupom, o valor mínimo da compra deve ser R$ ${appliedCoupon.minPurchase?.toFixed(2).replace(".", ",")}`
  }

  const handleProductClick = (productId: number, currentQuantity: number) => {
    onUpdateQuantity(productId, currentQuantity + 1)
  }

  return (
      <div className="flex gap-6 w-full h-full max-w-[1400px] p-6">
        <div className="flex-1 space-y-4">
          <Card className="border-2">
            <CardContent className="p-6">
              <div className="flex items-center gap-3 mb-2">
                <Scan className="h-6 w-6 text-[#2d5d3d]" />
                <h2 className="text-2xl font-bold">Escaneie seus produtos</h2>
              </div>
              <p className="text-base text-gray-500">Posicione o código de barras em frente ao leitor</p>
            </CardContent>
          </Card>

          <div className="flex gap-4">
            <Button
                variant="outline"
                className="flex-1 py-6 bg-[#2d5d3d] hover:bg-[#224731] text-white text-lg"
                onClick={onMyPointsClick}
            >
              <Award className="mr-2 h-5 w-5" /> Meus Pontos
            </Button>

            <Button
                variant="outline"
                className="flex-1 py-6 bg-[#2d5d3d] hover:bg-[#224731] text-white text-lg"
                onClick={onBarcodeInputClick}
            >
              <Barcode className="mr-2 h-5 w-5" /> Inserir Código
            </Button>
          </div>

          {notification && (
              <div className="bg-red-100 text-red-700 p-3 rounded-md text-sm">
                {notification}
              </div>
          )}

          <Card className="border-2">
            <CardContent className="p-4">
              <div
                  className="grid grid-cols-1 gap-4 max-h-[400px] overflow-y-auto"
                  style={{ scrollbarWidth: "thin" }}
              >
                {[...cart].reverse().map((item) => (
                    <div
                        key={item.id}
                        className="border rounded-lg p-4 flex items-center cursor-pointer hover:bg-gray-50"
                        onClick={() => handleProductClick(item.id, item.quantity)}
                    >
                      <div className="flex items-center gap-4 flex-1">
                        <div className="w-16 h-16 relative rounded-md overflow-hidden">
                          <Image
                              src={item.image || getProductImage(item.id)}
                              alt={item.name}
                              fill
                              style={{ objectFit: "contain" }}
                              onError={() => console.error(`Erro ao carregar imagem para ${item.name}`)}
                          />
                        </div>
                        <div>
                          <div className="font-medium text-base">{item.name}</div>
                        </div>
                      </div>

                      <div className="flex items-center gap-8 mr-4">
                        <div className="flex flex-col items-center">
                          <span className="text-base font-medium text-black">Quantidade</span>
                          <span className="font-bold text-lg text-black">{item.quantity}</span>
                        </div>

                        <div className="flex flex-col items-center">
                          <span className="text-base font-medium text-black">Preço</span>
                          <span className="font-bold text-lg text-black">
                        R$ {item.price.toFixed(2).replace(".", ",")}
                      </span>
                        </div>

                        <Button
                            variant="destructive"
                            size="icon"
                            className="rounded-full w-8 h-8"
                            onClick={(e) => {
                              e.stopPropagation()
                              onRemoveProduct(item.id)
                            }}
                        >
                          <Trash2 className="h-4 w-4" />
                        </Button>
                      </div>
                    </div>
                ))}
              </div>
            </CardContent>
          </Card>
        </div>

        <div className="w-[350px] space-y-4">
          <Card className="border-2">
            <CardContent className="p-6">
              <div className="space-y-4">
                <div className="flex justify-between text-base">
                  <span>Subtotal</span>
                  <span>R$ {calculateSubtotal().toFixed(2).replace(".", ",")}</span>
                </div>

                {appliedCoupon && (
                    <div className="flex flex-col gap-1">
                      <div className="flex justify-between text-base text-[#2d5d3d]">
                        <span>Desconto ({appliedCoupon.name})</span>
                        <span>- R$ {calculateDiscount().toFixed(2).replace(".", ",")}</span>
                      </div>
                      {!isCouponValid() && <div className="text-xs text-red-500">{getCouponStatusMessage()}</div>}
                    </div>
                )}

                <div className="border-t pt-4 flex justify-between font-bold text-lg">
                  <span>Total</span>
                  <span>R$ {calculateTotal().toFixed(2).replace(".", ",")}</span>
                </div>

                {pointsToEarn > 0 && (
                    <div className="bg-[#f0f7f3] p-3 rounded-md text-[#2d5d3d] text-sm mt-2">
                      <span className="font-medium">Você ganhará {pointsToEarn} pontos com esta compra!</span>
                    </div>
                )}

                <Button
                    onClick={onCheckout}
                    className="w-full bg-[#2d5d3d] hover:bg-[#224731] text-white py-4 text-base mt-4"
                >
                  Finalizar
                </Button>

                <Button variant="destructive" className="w-full py-4 text-base" onClick={onCancel}>
                  Cancelar
                </Button>
              </div>
            </CardContent>
          </Card>

          <Button onClick={onAddBeerClick} className="w-full bg-[#2d5d3d] hover:bg-[#224731] text-white py-4 text-base">
            <Beer className="mr-2 h-4 w-4" /> Adicionar Cerveja
          </Button>
        </div>
      </div>
  )
}