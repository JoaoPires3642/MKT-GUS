"use client"

import { Button } from "@/components/ui/button"
import type { ConfirmPurchaseResult } from "@/lib/types"

interface SuccessScreenProps {
  order: ConfirmPurchaseResult
  onNewPurchase: () => void
}

export default function SuccessScreen({ order, onNewPurchase }: SuccessScreenProps) {
  const isIssued = order.taxDocument?.status === "ISSUED"

  return (
    <div className="flex w-full max-w-[760px] flex-col items-center justify-center gap-6 rounded-3xl border bg-card p-10 text-center shadow-sm">
      <div className="space-y-3">
        <h1 className="text-4xl font-bold">Compra Finalizada</h1>
        <p className="text-xl text-muted-foreground">
          {isIssued ? "Imprimindo nota fiscal..." : "Pagamento aprovado. Imprimindo nota fiscal..."}
        </p>
      </div>

      <Button onClick={onNewPurchase} className="px-8 py-4 text-lg">
        Nova Compra
      </Button>
    </div>
  )
}
