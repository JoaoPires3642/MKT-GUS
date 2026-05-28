"use client"

import type React from "react"

import { useState } from "react"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Card, CardContent } from "@/components/ui/card"
import { Barcode } from "lucide-react"
import NumericKeypadDialog from "@/components/numeric-keypad-dialog"
import { onlyDigits } from "@/lib/numeric-input"

interface BarcodeInputPopupProps {
  onSubmit: (barcode: string) => void
  onCancel: () => void
  title?: string
  description?: string
  placeholder?: string
  submitLabel?: string
}

export default function BarcodeInputPopup({
  onSubmit,
  onCancel,
  title = "Inserir Código de Barras",
  description = "Toque no campo para abrir o teclado numérico.",
  placeholder = "Digite o código de barras",
  submitLabel = "Adicionar",
}: BarcodeInputPopupProps) {
  const [barcode, setBarcode] = useState("")
  const [showKeypad, setShowKeypad] = useState(false)

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    const normalizedBarcode = onlyDigits(barcode)
    if (normalizedBarcode.trim()) {
      onSubmit(normalizedBarcode)
    }
  }

  return (
    <>
      <Card className="relative w-[min(92vw,560px)] rounded-2xl border-2 shadow-sm">
        <CardContent className="p-8">
          <form onSubmit={handleSubmit} className="space-y-6">
            <div className="space-y-3 text-center">
              <div className="mb-2 flex justify-center">
                <Barcode className="h-10 w-10 text-primary" />
              </div>
              <h2 className="text-2xl font-bold">{title}</h2>
              <p className="text-base text-gray-500">{description}</p>
            </div>

            <div className="relative overflow-visible">
              <Input
                type="text"
                placeholder={placeholder}
                value={barcode}
                onClick={() => setShowKeypad(true)}
                readOnly
                inputMode="none"
                className="cursor-pointer py-6 text-center text-xl border-2"
              />

              <NumericKeypadDialog
                open={showKeypad}
                onOpenChange={setShowKeypad}
                value={barcode}
                onValueChange={setBarcode}
                onConfirm={() => setShowKeypad(false)}
                confirmLabel="Aplicar"
                maxLength={14}
              />
            </div>

          <div className="flex justify-center gap-4">
            <Button type="button" variant="destructive" onClick={onCancel} className="px-6 py-3 rounded-md text-base">
              Cancelar
            </Button>
              <Button type="submit" className="px-6 py-3 rounded-md text-base" disabled={!barcode.trim()}>
                {submitLabel}
              </Button>
            </div>
          </form>
        </CardContent>
      </Card>
    </>
  )
}
