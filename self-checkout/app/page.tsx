"use client"

import { useState, useEffect } from "react"
import axios from "axios"
import { Client } from "@stomp/stompjs"
import WelcomeScreen from "@/components/welcome-screen"
import CpfScreen from "@/components/cpf-screen"
import ScanningScreen from "@/components/scanning-screen"
import PaymentScreen from "@/components/payment-screen"
import SuccessScreen from "@/components/success-screen"
import CancelConfirmation from "@/components/cancel-confirmation"
import AgeVerificationPopup from "@/components/age-verification-popup"
import PointsBlockedPopup from "@/components/points-blocked-popup"
import MyPointsPopup from "@/components/my-points-popup"
import CpfInputPopup from "@/components/cpf-input-popup"
import BarcodeInputPopup from "@/components/barcode-input-popup"
import type { Product, Coupon } from "@/lib/types"

export default function Home() {
  const [currentScreen, setCurrentScreen] = useState<"welcome" | "scanning" | "payment" | "success">("welcome")
  const [showCpfPopup, setShowCpfPopup] = useState(false)
  const [showCancelPopup, setShowCancelPopup] = useState(false)
  const [showAgeVerificationPopup, setShowAgeVerificationPopup] = useState(false)
  const [showPointsBlockedPopup, setShowPointsBlockedPopup] = useState(false)
  const [showMyPointsPopup, setShowMyPointsPopup] = useState(false)
  const [showCpfInputPopup, setShowCpfInputPopup] = useState(false)
  const [showBarcodeInputPopup, setShowBarcodeInputPopup] = useState(false)
  const [cpf, setCpf] = useState("")
  const [cart, setCart] = useState<Product[]>([])
  const [hasCpf, setHasCpf] = useState(false)
  const [pointsBalance, setPointsBalance] = useState(1000)
  const [appliedCoupon, setAppliedCoupon] = useState<Coupon | null>(null)
  const [pointsToEarn, setPointsToEarn] = useState(0)
  const [notification, setNotification] = useState<string | null>(null)

  useEffect(() => { //função de calcular os pontos
    const subtotal = cart.reduce((sum, item) => sum + item.price * item.quantity, 0)
    const newPointsToEarn = Math.floor(subtotal / 50) * 10
    setPointsToEarn(newPointsToEarn)
  }, [cart])


  // Função para enviar pontos ao backend
  const updatePointsInBackend = async (cpf: string, points: number) => {
    try {
      const response = await fetch("http://localhost:8080/api/pontos/finalizar-compra", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          cpf,
          pointsBalance: points,
        }),
      });

      if (!response.ok) {
        throw new Error("Erro ao atualizar pontos no backend");
      }

      const data = await response.json();
      console.log("Pontos atualizados com sucesso:", data);
      return true;
    } catch (error) {
      console.error("Erro ao enviar pontos:", error);
      alert("Não foi possível atualizar os pontos. Tente novamente.");
      return false;
    }
  };



  useEffect(() => {
    if (notification) {
      const timer = setTimeout(() => setNotification(null), 3000)
      return () => clearTimeout(timer)
    }
  }, [notification])

  // Configurar WebSocket com @stomp/stompjs
  useEffect(() => {
    const stompClient = new Client({
      brokerURL: "ws://localhost:8080/ws",
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
      onConnect: () => {
        stompClient.subscribe("/topic/scanned-product", (message) => {
          const data = JSON.parse(message.body)
          if (data.ean) {
            // Produto recebido
            const newProduct: Product = {
              id: Date.now(),
              name: data.nome,
              price: data.valor,
              quantity: 1,
              image: data.urlImagem,
              ean: data.ean,
              isAdult: data.produtoMaiorDeIdade,
            }
            addProduct(newProduct)
          } else if (data.message) {
            // Mensagem de erro
          //  setNotification(data.message)
          }
        })
      },
      onStompError: (frame) => {
        //console.error("Erro na conexão WebSocket:", frame)
      },
    })

    stompClient.activate()

    return () => {
      stompClient.deactivate()
    }
  }, [])

  const handleStartShopping = () => {
    setShowCpfPopup(true)
  }

// Update the handleCpfSubmit function to close popup and go to scanning
  const handleCpfSubmit = async (value: string) => {
    setCpf(value)
    setHasCpf(value.trim() !== "")
    setShowCpfPopup(false)
    setCurrentScreen("scanning")

    // Fazer uma requisição para o backend para obter os pontos do cliente
    try {
      const response = await fetch("http://localhost:8080/pessoa/verificar-cpf", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ cpf: value }),
      });

      if (!response.ok) {
        throw new Error("Erro ao verificar CPF");
      }

      const data = await response.json();
      setPointsBalance(data); // Atualiza o saldo de pontos com a resposta do backend
    } catch (error) {
      console.error("Erro ao buscar pontos do cliente:", error);
    }
  }

  const handleCpfInputSubmit = (value: string) => {
    setCpf(value)
    setHasCpf(value.trim() !== "")
    setShowCpfInputPopup(false)
    setShowMyPointsPopup(true)
  }

  const handleCancelClick = () => {
    setShowCancelPopup(true)
  }

  const handleCancelConfirm = (confirmed: boolean) => {
    setShowCancelPopup(false)
    if (confirmed) {

      //limpa o carrinho e cupom
      setCart([])
      setAppliedCoupon(null)
      setCurrentScreen("welcome") //volta para o inico
    }
  }

  const handleAddBeerClick = () => {
    setShowAgeVerificationPopup(true)
  }

  const handleAgeVerificationConfirm = async () => {
    setShowAgeVerificationPopup(false);
    try {
      // Fazer requisição POST para buscar o produto com o EAN especificado
      const response = await axios.post("http://localhost:8080/produtos/buscar", {
        barcode: "07896045506248",
      });

      const data = response.data;
      if (data.ean) {
        // Produto recebido
        const newProduct: Product = {
          id: Date.now(),
          name: data.nome || "Beck's Beer",
          price: data.valor || 4.5, // Preço padrão caso a API não retorne
          quantity: 1,
          image: data.urlImagem || "", // Imagem padrão caso a API não retorne
          ean: data.ean,
          isAdult: data.produtoMaiorDeIdade || true, // Assume que é produto adulto
        };
        addProduct(newProduct);
      } else {
        setNotification(data.message || "Produto não encontrado.");
      }
    } catch (error) {
      console.error("Erro ao buscar cerveja:", error);
      setNotification("Erro ao adicionar cerveja. Tente novamente.");
    }
  };

  const handleAgeVerificationCancel = () => {
    setShowAgeVerificationPopup(false)
  }

  const handleMyPointsClick = () => {
    if (!hasCpf) {
      setShowPointsBlockedPopup(true)
    } else {
      setShowMyPointsPopup(true)
    }
  }

  const handlePointsBlockedClose = () => {
    setShowPointsBlockedPopup(false)
  }

  const handleInsertCpf = () => {
    setShowPointsBlockedPopup(false)
    setShowCpfInputPopup(true)
  }

  const handleCpfInputCancel = () => {
    setShowCpfInputPopup(false)
  }

  const handleApplyCoupon = (coupon: Coupon) => {
    const subtotal = cart.reduce((sum, item) => sum + item.price * item.quantity, 0);
    if (pointsBalance < (coupon.pointsCost || 0)) {
      setNotification("Saldo de pontos insuficiente para aplicar este cupom.");
      return;
    }
    if (coupon.minPurchase && subtotal < coupon.minPurchase) {
      setNotification(`O valor da compra (R$${subtotal.toFixed(2)}) é menor que o mínimo necessário (R$${coupon.minPurchase.toFixed(2)}).`);
      return;
    }
    setAppliedCoupon(coupon);
  };

  const handleCheckout = () => {
    setCurrentScreen("payment")
  }

  const handlePaymentConfirm = async () => {
    const subtotal = cart.reduce((sum, item) => sum + item.price * item.quantity, 0);
    const pointsDeducted = appliedCoupon ? (appliedCoupon.pointsCost || 0) : 0;

    if (pointsDeducted > pointsBalance) {
      setNotification("Saldo de pontos insuficiente para aplicar este cupom.");
      return;
    }

    const newPointsBalance = pointsBalance - pointsDeducted + pointsToEarn;

    const pedidoPayload = {
      clienteCpf: hasCpf && cpf ? cpf.replace(/\D/g, "") : null,
      itens: cart.map((item) => ({
        ean: item.ean,
        quantidade: item.quantity,
        valorUnitario: item.price,
      })),
      cupom: appliedCoupon
          ? {
            id: parseInt(appliedCoupon.id),
            desconto: appliedCoupon.value, // Enviar o valor bruto
            tipoDesconto: appliedCoupon.type,
          }
          : null,
    };

    try {
      // Primeira requisição: confirmar compra
      const response = await fetch("http://localhost:8080/pedidos/confirmar-compra", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(pedidoPayload),
      });

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || "Erro ao confirmar o pedido");
      }

      const pedido = await response.json();
      console.log("Pedido confirmado:", pedido);

      // Segunda requisição: atualizar pontos
      if (hasCpf && cpf) {
        const pointsResponse = await fetch("http://localhost:8080/api/pontos/finalizar-compra", {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({
            cpf: cpf.replace(/\D/g, ""),
            pontosNecessarios: newPointsBalance,
          }),
        });

        if (!pointsResponse.ok) {
          const errorText = await pointsResponse.text();
          let errorData;
          try {
            errorData = JSON.parse(errorText);
            throw new Error(errorData.message || "Erro ao atualizar pontos");
          } catch {
            throw new Error(`Erro ao atualizar pontos: ${errorText}`);
          }
        }

        const pointsData = await pointsResponse.json();
        console.log("Pontos atualizados:", pointsData);
        setPointsBalance(newPointsBalance);
      }

      setCart([]);
      setAppliedCoupon(null);
      setCurrentScreen("success");
    } catch (error: any) {
      console.error("Erro ao processar pagamento:", error);
      setNotification(error.message || "Erro ao finalizar a compra. Tente novamente.");
    }
  };

  const handleNewPurchase = () => {
    setCart([])
    setAppliedCoupon(null)
    setCurrentScreen("welcome")
  }

  const updateProductQuantity = (productId: number, newQuantity: number) => {
    if (newQuantity <= 0) {
      removeProduct(productId)
      return
    }
    setCart(cart.map((item) => (item.id === productId ? { ...item, quantity: newQuantity } : item)))
  }

  const removeProduct = (productId: number) => {
    setCart(cart.filter((item) => item.id !== productId))
  }

  const addProduct = (product: Product) => {
    const existingProduct = cart.find((item) => item.ean === product.ean)
    if (existingProduct) {
      updateProductQuantity(existingProduct.id, existingProduct.quantity + 1)
    } else {
      setCart([...cart, product])
    }
  }

  const handleBarcodeInputClick = () => {
    setShowBarcodeInputPopup(true)
  }

  const handleBarcodeSubmit = async (barcode: string) => {
    setShowBarcodeInputPopup(false)
    try {
      // Fazer requisição POST para o backend
      await axios.post("http://localhost:8080/produtos/buscar", { barcode })
      // A resposta será processada via WebSocket
    } catch (error) {
      console.error("Erro ao buscar produto:", error)
      setNotification(`Erro ao buscar produto para o código: ${barcode}`)
    }
  }

  const handleBarcodeInputCancel = () => {
    setShowBarcodeInputPopup(false)
  }

  return (
      <main className="w-screen h-screen flex items-center justify-center bg-[#f8f7f2] overflow-hidden">
        <div className="w-full h-full max-w-[1920px] max-h-[1080px] flex items-center justify-center p-6">
          {currentScreen === "welcome" && <WelcomeScreen onStartShopping={handleStartShopping} />}
          {currentScreen === "scanning" && (
              <ScanningScreen
                  cart={cart}
                  onUpdateQuantity={updateProductQuantity}
                  onRemoveProduct={removeProduct}
                  onAddProduct={addProduct}
                  onCheckout={handleCheckout}
                  onCancel={handleCancelClick}
                  onAddBeerClick={handleAddBeerClick}
                  onMyPointsClick={handleMyPointsClick}
                  onBarcodeInputClick={handleBarcodeInputClick}
                  appliedCoupon={appliedCoupon}
                  pointsToEarn={pointsToEarn}
              />
          )}
          {currentScreen === "payment" && (
              <PaymentScreen
                  onConfirm={handlePaymentConfirm}
                  onBack={() => setCurrentScreen("scanning")}
                  cpf={cpf}
                  appliedCoupon={appliedCoupon}
              />
          )}
          {currentScreen === "success" && <SuccessScreen onNewPurchase={handleNewPurchase} />}
          {showCpfPopup && (
              <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
                <CpfScreen onSubmit={handleCpfSubmit} onCancel={() => setShowCpfPopup(false)} />
              </div>
          )}
          {showCancelPopup && (
              <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
                <CancelConfirmation onConfirm={handleCancelConfirm} />
              </div>
          )}
          {showAgeVerificationPopup && (
              <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
                <AgeVerificationPopup onConfirm={handleAgeVerificationConfirm} onCancel={handleAgeVerificationCancel} />
              </div>
          )}
          {showPointsBlockedPopup && (
              <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
                <PointsBlockedPopup onClose={handlePointsBlockedClose} onInsertCpf={handleInsertCpf} />
              </div>
          )}
          {showCpfInputPopup && (
              <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
                <CpfInputPopup onSubmit={handleCpfInputSubmit} onCancel={handleCpfInputCancel} />
              </div>
          )}
          {showMyPointsPopup && (
              <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
                <MyPointsPopup
                    onClose={() => setShowMyPointsPopup(false)}
                    pointsBalance={pointsBalance}
                    onApplyCoupon={handleApplyCoupon}
                    appliedCoupon={appliedCoupon}
                    setPointsBalance={setPointsBalance}
                    subtotal={cart.reduce((sum, item) => sum + item.price * item.quantity, 0)} // Passe o subtotal
                />
              </div>
          )}
          {showBarcodeInputPopup && (
              <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
                <BarcodeInputPopup onSubmit={handleBarcodeSubmit} onCancel={handleBarcodeInputCancel} />
              </div>
          )}
        </div>
      </main>
  );
}