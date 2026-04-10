"use client"

import type React from "react"

import { useState } from "react"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Card, CardContent } from "@/components/ui/card"
import { Barcode } from "lucide-react"

interface BarcodeInputPopupProps {
  onSubmit: (barcode: string) => void
  onCancel: () => void
}

export default function BarcodeInputPopup({ onSubmit, onCancel }: BarcodeInputPopupProps) {
  const [barcode, setBarcode] = useState("")

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    if (barcode.trim()) {
      onSubmit(barcode)
    }
  }

  return (
    <Card className="relative border-2 rounded-lg shadow-sm w-[500px]">
      <CardContent className="p-8">
        <form onSubmit={handleSubmit} className="space-y-6">
          <div className="space-y-3 text-center">
            <div className="flex justify-center mb-2">
              <Barcode className="h-10 w-10 text-[#2d5d3d]" />
            </div>
            <h2 className="text-2xl font-bold">Inserir Código de Barras</h2>
            <p className="text-gray-500 text-base">Digite o código de barras do produto manualmente.</p>
          </div>

          <Input
            type="text"
            placeholder="Digite o código de barras"
            value={barcode}
            onChange={(e) => setBarcode(e.target.value)}
            className="text-center py-6 text-xl border-2"
            autoFocus
          />

          <div className="flex justify-center gap-4">
            <Button type="button" variant="destructive" onClick={onCancel} className="px-6 py-3 rounded-md text-base">
              Cancelar
            </Button>
            <Button
              type="submit"
              className="bg-[#2d5d3d] hover:bg-[#224731] text-white px-6 py-3 rounded-md text-base"
              disabled={!barcode.trim()}
            >
              Adicionar
            </Button>
          </div>
        </form>
      </CardContent>
    </Card>
  )
}
