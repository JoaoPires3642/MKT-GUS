"use client"

import { useMemo, useState } from "react"
import { Button } from "@/components/ui/button"
import { Card, CardContent } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import type { PriceOverride, Product } from "@/lib/types"

const PRICE_OVERRIDE_REASONS = [
  { value: "ETIQUETA_PROMOCIONAL_NAO_ATUALIZADA", label: "Etiqueta promocional nao atualizada" },
  { value: "PROMOCAO_NAO_SINCRONIZADA", label: "Promocao nao sincronizada" },
  { value: "ETIQUETA_DESATUALIZADA", label: "Etiqueta desatualizada" },
  { value: "AJUSTE_OPERACIONAL", label: "Ajuste operacional" },
] as const

interface AuthorizePricePopupProps {
  product: Product
  employeeRegistration: string
  onCancel: () => void
  onConfirm: (productId: number, newPrice: number, priceOverride: PriceOverride) => void
}

export default function AuthorizePricePopup({ product, employeeRegistration, onCancel, onConfirm }: AuthorizePricePopupProps) {
  const [newPrice, setNewPrice] = useState(product.price.toFixed(2).replace(".", ","))
  const [reason, setReason] = useState(product.priceOverride?.reason ?? "ETIQUETA_PROMOCIONAL_NAO_ATUALIZADA")
  const [error, setError] = useState<string | null>(null)

  const referencePrice = useMemo(() => product.originalPrice ?? product.price, [product.originalPrice, product.price])

  const handleConfirm = () => {
    const normalizedPrice = Number.parseFloat(newPrice.replace(",", "."))

    if (!Number.isFinite(normalizedPrice) || normalizedPrice <= 0) {
      setError("Informe um valor válido para o item.")
      return
    }

    if (!reason.trim()) {
      setError("Informe o motivo do ajuste.")
      return
    }
    setError(null)
    onConfirm(product.id, normalizedPrice, {
      employeeRegistration,
      authorizedUnitPrice: normalizedPrice,
      reason: reason.trim(),
    })
  }

  return (
    <Card className="relative border-2 rounded-lg shadow-sm w-[560px]">
      <CardContent className="p-8 space-y-6">
        <div className="space-y-2">
          <h2 className="text-2xl font-bold">Ajuste de preço autorizado</h2>
          <p className="text-base text-gray-500">Aplique um valor excepcional apenas para esta compra.</p>
        </div>

        <div className="rounded-md bg-[#f0f7f3] p-4 text-sm text-[#2d5d3d]">
          <div className="font-medium">{product.name}</div>
          <div>Preço de referência: R$ {referencePrice.toFixed(2).replace(".", ",")}</div>
        </div>

        <div className="rounded-md bg-[#f0f7f3] p-4 text-sm text-[#2d5d3d]">
          Matrícula autorizada: {employeeRegistration}
        </div>

        <Input
          type="text"
          placeholder="Novo valor"
          value={newPrice}
          onChange={(event) => setNewPrice(event.target.value)}
        />

        <Select value={reason} onValueChange={setReason}>
          <SelectTrigger>
            <SelectValue placeholder="Selecione o motivo do ajuste" />
          </SelectTrigger>
          <SelectContent>
            {PRICE_OVERRIDE_REASONS.map((reasonOption) => (
              <SelectItem key={reasonOption.value} value={reasonOption.value}>
                {reasonOption.label}
              </SelectItem>
            ))}
          </SelectContent>
        </Select>

        {error && <p className="text-sm text-red-500">{error}</p>}

        <div className="flex justify-end gap-4">
          <Button type="button" variant="destructive" onClick={onCancel}>
            Cancelar
          </Button>
          <Button type="button" className="bg-[#2d5d3d] hover:bg-[#224731] text-white" onClick={handleConfirm}>
            Aplicar ajuste
          </Button>
        </div>
      </CardContent>
    </Card>
  )
}
