"use client"

import { Button } from "@/components/ui/button"
import { Card, CardContent } from "@/components/ui/card"

interface CancelConfirmationProps {
  onConfirm: (confirmed: boolean) => void
}

export default function CancelConfirmation({ onConfirm }: CancelConfirmationProps) {
  return (
    <Card className="relative border-2 rounded-lg shadow-sm w-[400px]">
      <CardContent className="p-8">
        <div className="space-y-6 text-center">
          <h2 className="text-2xl font-bold">Cancelar compra?</h2>

          <div className="flex justify-center gap-4 pt-2">
            <Button onClick={() => onConfirm(true)} variant="destructive" className="px-6 py-3 rounded-md text-base">
              Sim
            </Button>
            <Button
              onClick={() => onConfirm(false)}
              className="bg-[#2d5d3d] hover:bg-[#224731] text-white px-6 py-3 rounded-md text-base"
            >
              Não
            </Button>
          </div>
        </div>
      </CardContent>
    </Card>
  )
}
