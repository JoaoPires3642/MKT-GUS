"use client"

import { useState } from "react"
import { Button } from "@/components/ui/button"
import { Card, CardContent } from "@/components/ui/card"
import { Check, CreditCard, Landmark, QrCode, Wallet } from "lucide-react"
import type { Coupon, PaymentMethod, PaymentStatus } from "@/lib/types"

interface PaymentScreenProps {
  onConfirm: (method: PaymentMethod) => void
  onBack: () => void
  cpf?: string
  appliedCoupon: Coupon | null
  totalAmount: number
  paymentStatus: PaymentStatus | null
  paymentError: string | null
  isProcessingPayment: boolean
}

const METHODS: Array<{ icon: typeof CreditCard; label: string; value: PaymentMethod }> = [
  { icon: CreditCard, label: "Cartao de Credito", value: "CREDIT" },
  { icon: Landmark, label: "Cartao de Debito", value: "DEBIT" },
  { icon: Wallet, label: "Vale", value: "VALE" },
  { icon: QrCode, label: "PIX", value: "PIX" },
]

function getPaymentMessage(paymentStatus: PaymentStatus | null, paymentError: string | null) {
  if (paymentError) {
    return paymentError
  }

  switch (paymentStatus) {
    case "PROCESSING":
      return "Aguardando confirmacao do pagamento digital..."
    case "PAID":
    case "AUTHORIZED":
      return "Pagamento confirmado. Finalizando compra..."
    case "FAILED":
    case "EXPIRED":
      return "O pagamento nao foi concluido. Tente novamente."
    case "CANCELED":
      return "Pagamento cancelado. Escolha outra forma de pagamento ou tente novamente."
    default:
      return null
  }
}

export default function PaymentScreen({
  onConfirm,
  onBack,
  appliedCoupon,
  totalAmount,
  paymentStatus,
  paymentError,
  isProcessingPayment,
}: PaymentScreenProps) {
  const [selectedPaymentMethod, setSelectedPaymentMethod] = useState<PaymentMethod | null>(null)
  const paymentMessage = getPaymentMessage(paymentStatus, paymentError)

  return (
    <div className="w-[800px] flex flex-col justify-center space-y-6 p-6">
      <div className="space-y-2">
        <h2 className="text-2xl font-bold">Escolha o metodo de pagamento</h2>
        <p className="text-base text-muted-foreground">Selecione como deseja pagar para iniciar a cobranca digital.</p>
      </div>

      <div className="rounded-xl border bg-card p-4 text-base font-medium">
        Total a pagar: R$ {totalAmount.toFixed(2).replace(".", ",")}
        {appliedCoupon && <span className="block text-sm font-normal text-muted-foreground">Cupom aplicado: {appliedCoupon.name}</span>}
      </div>

      <div className="space-y-4">
        {METHODS.map((method) => {
          const Icon = method.icon
          const isSelected = selectedPaymentMethod === method.value

          return (
            <Card
              key={method.value}
              className={`border-2 cursor-pointer transition-all ${isSelected ? "border-primary bg-primary/10" : "hover:border-primary"} ${isProcessingPayment ? "pointer-events-none opacity-60" : ""}`}
              onClick={() => setSelectedPaymentMethod(method.value)}
            >
              <CardContent className="p-6 flex justify-between items-center">
                <div className="flex items-center gap-3">
                  <Icon className="h-6 w-6 text-primary" />
                  <h3 className="text-lg font-medium">{method.label}</h3>
                </div>
                {isSelected && (
                  <div className="w-6 h-6 rounded-full bg-primary flex items-center justify-center">
                    <Check className="h-4 w-4 text-white" />
                  </div>
                )}
              </CardContent>
            </Card>
          )
        })}
      </div>

      {paymentMessage && (
        <div className={`rounded-md p-4 text-sm ${paymentError ? "bg-red-100 text-red-700" : "bg-primary/10 text-primary"}`}>
          {paymentMessage}
        </div>
      )}

      <div className="flex justify-between gap-4 pt-4 mt-6">
        <Button
          variant="outline"
          onClick={onBack}
          className="flex-1 py-4 bg-gray-200 hover:bg-gray-300 border-0 text-base"
          disabled={isProcessingPayment}
        >
          Voltar
        </Button>

        <Button
          onClick={() => selectedPaymentMethod && onConfirm(selectedPaymentMethod)}
          className="flex-1 py-4 text-base"
          disabled={selectedPaymentMethod === null || isProcessingPayment}
        >
          {isProcessingPayment ? "Processando..." : "Iniciar Pagamento"}
        </Button>
      </div>
    </div>
  )
}
