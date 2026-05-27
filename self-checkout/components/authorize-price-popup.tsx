"use client"

import { useMemo, useState } from "react"
import { Button } from "@/components/ui/button"
import { Card, CardContent } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { PRICE_OVERRIDE_REASONS } from "@/lib/price-override-reasons"
import type { PriceOverride, Product } from "@/lib/types"

interface AuthorizePricePopupProps {
  product: Product
  employeeRegistration: string
  employeeName: string
  onCancel: () => void
  onConfirm: (productId: number, newPrice: number, priceOverride: PriceOverride) => void
}

export default function AuthorizePricePopup({ product, employeeRegistration, employeeName, onCancel, onConfirm }: AuthorizePricePopupProps) {
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

        <div className="rounded-md bg-primary/10 p-4 text-sm text-primary">
          <div className="font-medium">{product.name}</div>
          <div>Preço de referência: R$ {referencePrice.toFixed(2).replace(".", ",")}</div>
        </div>

        <div className="rounded-md bg-primary/10 p-4 text-sm text-primary">
          Autorizado por: {employeeName || employeeRegistration}
        </div>

        <div className="rounded-md bg-amber-50 p-4 text-sm text-amber-800">
          O cliente sera informado de que este item recebeu um ajuste manual autorizado.
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
          <Button type="button" onClick={handleConfirm}>
            Aplicar ajuste
          </Button>
        </div>
      </CardContent>
    </Card>
  )
}
