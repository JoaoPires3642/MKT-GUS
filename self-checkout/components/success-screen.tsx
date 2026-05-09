"use client"

import { useEffect } from "react"
import { Button } from "@/components/ui/button"
import type { ConfirmPurchaseResult, PaymentMethod } from "@/lib/types"

const AUTO_CLOSE_MS = 15000

interface SuccessScreenProps {
  order: ConfirmPurchaseResult
  paymentMethod: PaymentMethod | null
  onNewPurchase: () => void
}

function formatCurrency(value: number) {
  return new Intl.NumberFormat("pt-BR", {
    style: "currency",
    currency: "BRL",
  }).format(value)
}

function formatCpf(value: number | null) {
  if (!value) {
    return "Consumidor nao identificado"
  }

  const digits = value.toString().padStart(11, "0")
  return digits.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, "$1.$2.$3-$4")
}

function formatDateTime(value: string) {
  return new Intl.DateTimeFormat("pt-BR", {
    dateStyle: "short",
    timeStyle: "short",
  }).format(new Date(value))
}

function getPaymentMethodLabel(paymentMethod: PaymentMethod | null) {
  switch (paymentMethod) {
    case "CREDIT":
      return "Cartao de credito"
    case "DEBIT":
      return "Cartao de debito"
    case "VALE":
      return "Vale"
    case "PIX":
      return "PIX"
    default:
      return "Nao informado"
  }
}

export default function SuccessScreen({ order, paymentMethod, onNewPurchase }: SuccessScreenProps) {
  const taxStatus = order.taxDocument?.status ?? "PENDING"
  const isIssued = taxStatus === "ISSUED"

  useEffect(() => {
    const timer = window.setTimeout(() => onNewPurchase(), AUTO_CLOSE_MS)
    return () => window.clearTimeout(timer)
  }, [onNewPurchase])

  return (
    <div className="print-screen flex w-full max-w-[1200px] flex-col gap-6 p-6 print:max-w-none print:p-0">
      <div className="fixed inset-0 z-40 flex items-center justify-center bg-black/50 p-6 print:hidden">
        <div className="w-full max-w-[760px] rounded-3xl border bg-card p-8 shadow-sm">
          <div className="flex flex-col items-center text-center gap-6">
            <div className="space-y-3">
              <h1 className="text-4xl font-bold">Deseja imprimir a nota fiscal?</h1>
              <p className="text-lg text-muted-foreground">Imprima a DANFE simplificada da compra ou inicie uma nova compra.</p>
            </div>

            <div className="flex w-full max-w-[520px] justify-center gap-4">
              <Button variant="outline" onClick={() => window.print()} className="min-w-[220px] py-6 text-lg">
                Imprimir
              </Button>
              <Button onClick={onNewPurchase} className="min-w-[220px] py-6 text-lg">
                Nova Compra
              </Button>
            </div>
          </div>
        </div>
      </div>

      <div className="hidden print:block">
        <div>
          <h1 className="text-3xl font-bold">DANFE Simplificada</h1>
          <p className="text-muted-foreground">Nota fiscal da compra realizada no autoatendimento.</p>
        </div>
      </div>

      <section className="print-document hidden mx-auto w-full max-w-[820px] bg-white p-8 text-black shadow-sm print:block print:max-w-none print:p-0 print:shadow-none">
        <header className="border-b border-dashed border-black pb-4 text-center">
          <h2 className="text-2xl font-bold uppercase tracking-[0.2em]">DANFE Simplificada</h2>
          <p className="mt-2 text-sm">Representacao da nota fiscal da compra no autoatendimento</p>
        </header>

        <div className="mt-4 grid gap-2 text-sm sm:grid-cols-2 print:grid-cols-2">
          <div>
            <span className="font-semibold">Pedido:</span> #{order.id}
          </div>
          <div>
            <span className="font-semibold">Emitido em:</span> {formatDateTime(order.orderedAt)}
          </div>
          <div>
            <span className="font-semibold">CPF:</span> {formatCpf(order.customerCpf)}
          </div>
          <div>
            <span className="font-semibold">Status fiscal:</span> {isIssued ? "Emitida" : "Processando emissao"}
          </div>
          <div>
            <span className="font-semibold">Pagamento:</span> {getPaymentMethodLabel(paymentMethod)}
          </div>
          {order.taxDocument?.numeroDocumento && (
            <div>
              <span className="font-semibold">Numero:</span> {order.taxDocument.numeroDocumento}
            </div>
          )}
          {order.taxDocument?.chaveAcesso && (
            <div>
              <span className="font-semibold">Chave:</span> {order.taxDocument.chaveAcesso}
            </div>
          )}
        </div>

        <div className="mt-6 border-y border-dashed border-black py-3">
          <div className="grid grid-cols-[1fr_auto_auto] gap-3 text-xs font-semibold uppercase tracking-wide">
            <span>Item</span>
            <span>Qtd x Unit.</span>
            <span>Total</span>
          </div>
        </div>

        <div className="space-y-3 py-4">
          {order.items.map((item, index) => (
            <div key={`${item.ean}-${index}`} className="grid grid-cols-[1fr_auto_auto] gap-3 text-sm">
              <div>
                <div className="font-medium">{item.productName}</div>
                <div className="text-xs text-neutral-600">EAN {item.ean}</div>
              </div>
              <div className="whitespace-nowrap">{item.quantity} x {formatCurrency(item.unitPrice)}</div>
              <div className="whitespace-nowrap text-right font-medium">{formatCurrency(item.totalPrice)}</div>
            </div>
          ))}
        </div>

        <div className="border-t border-dashed border-black pt-4">
          <div className="flex items-center justify-between text-base font-bold">
            <span>Total da compra</span>
            <span>{formatCurrency(order.totalAmount)}</span>
          </div>

          {typeof order.updatedPointsBalance === "number" && (
            <div className="mt-2 text-sm">
              <span className="font-semibold">Saldo de pontos atualizado:</span> {order.updatedPointsBalance}
            </div>
          )}

          {!isIssued && order.taxDocument?.motivoFalha && (
            <div className="mt-2 text-sm">
              <span className="font-semibold">Observacao:</span> {order.taxDocument.motivoFalha}
            </div>
          )}

          {order.taxDocument?.urlDanfe && (
            <div className="mt-2 break-all text-xs">
              <span className="font-semibold">Link DANFE:</span> {order.taxDocument.urlDanfe}
            </div>
          )}
        </div>

        <footer className="mt-6 border-t border-dashed border-black pt-4 text-center text-xs uppercase tracking-[0.2em]">
          Obrigado. Guarde este comprovante.
        </footer>
      </section>
    </div>
  )
}
