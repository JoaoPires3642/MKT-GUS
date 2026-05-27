"use client"

import { Card, CardContent } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import type { Product } from "@/lib/types"

interface EmployeeCartPopupProps {
  cart: Product[]
  employeeRegistration: string
  employeeName: string
  onSelectProduct: (product: Product) => void
  onClose: () => void
}

export default function EmployeeCartPopup({ cart, employeeRegistration, employeeName, onSelectProduct, onClose }: EmployeeCartPopupProps) {
  return (
    <Card className="relative border-2 rounded-lg shadow-sm w-[720px]">
      <CardContent className="p-8 space-y-6">
        <div className="space-y-2">
          <h2 className="text-2xl font-bold">Atendimento do funcionário</h2>
          <p className="text-base text-gray-500">Funcionário: {employeeName || employeeRegistration}. Selecione um item do carrinho para ajustar.</p>
        </div>

        <div className="rounded-md bg-primary/10 p-4 text-sm text-primary">
          Modo autorizacao ativo. O proximo item selecionado recebera um ajuste manual identificado por {employeeName || employeeRegistration}.
        </div>

        {cart.length === 0 ? (
          <div className="rounded-md bg-muted p-6 text-center text-muted-foreground">
            Nenhum produto no carrinho para ajuste.
          </div>
        ) : (
          <div className="space-y-3 max-h-[420px] overflow-y-auto">
            {cart.map((item) => (
              <button
                key={item.id}
                type="button"
                className="w-full rounded-lg border p-4 text-left hover:bg-gray-50"
                onClick={() => onSelectProduct(item)}
              >
                <div className="font-medium">{item.name}</div>
                <div className="text-sm text-gray-500">EAN: {item.ean}</div>
                <div className="text-sm text-gray-500">Quantidade: {item.quantity}</div>
                <div className="text-sm text-gray-500">Preço atual: R$ {item.price.toFixed(2).replace(".", ",")}</div>
              </button>
            ))}
          </div>
        )}

        <div className="flex justify-end">
          <Button type="button" variant="destructive" onClick={onClose}>Fechar</Button>
        </div>
      </CardContent>
    </Card>
  )
}
