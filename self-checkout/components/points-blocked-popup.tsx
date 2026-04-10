"use client"

import { Button } from "@/components/ui/button"
import { Card, CardContent } from "@/components/ui/card"

interface PointsBlockedPopupProps {
  onClose: () => void
  onInsertCpf: () => void
}

export default function PointsBlockedPopup({ onClose, onInsertCpf }: PointsBlockedPopupProps) {
  return (
    <Card className="relative border-2 rounded-lg shadow-sm w-[400px]">
      <CardContent className="p-8">
        <div className="space-y-6 text-center">
          <h2 className="text-2xl font-bold">Pontos Bloqueados</h2>

          <p className="text-gray-500 text-base">Para acessar seus pontos, é necessário informar o CPF.</p>

          <div className="flex justify-center gap-4 pt-2">
            <Button variant="destructive" onClick={onClose} className="px-6 py-3 rounded-md text-base">
              Continuar
            </Button>
            <Button
              onClick={onInsertCpf}
              className="bg-[#2d5d3d] hover:bg-[#224731] text-white px-6 py-3 rounded-md text-base"
            >
              Inserir CPF
            </Button>
          </div>
        </div>
      </CardContent>
    </Card>
  )
}
