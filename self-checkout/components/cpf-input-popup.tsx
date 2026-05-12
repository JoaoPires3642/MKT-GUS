"use client"

import type React from "react"
import { useState } from "react"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Card, CardContent } from "@/components/ui/card"
import NumericKeypadDialog from "@/components/numeric-keypad-dialog"
import { formatCpfDigits, onlyDigits } from "@/lib/numeric-input"

interface CpfInputPopupProps {
  onSubmit: (cpf: string) => void
  onCancel: () => void
}

export default function CpfInputPopup({ onSubmit, onCancel }: CpfInputPopupProps) {
  const [cpfDigits, setCpfDigits] = useState("")
  const [error, setError] = useState<string | null>(null)
  const [showKeypad, setShowKeypad] = useState(false)

  const handleChange = (value: string) => {
    setCpfDigits(onlyDigits(value).slice(0, 11))
    if (error) {
      setError(null)
    }
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    if (cpfDigits.length === 11) {
      try {
        const response = await fetch("http://localhost:8080/pessoa/verificar-cpf", {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({ cpf: cpfDigits }),
        })

        if (response.ok) {
          await response.json()
          onSubmit(cpfDigits)
        } else {
          const result = await response.json().catch(() => null)
          setError(result?.message ?? "CPF inválido")
        }
      } catch (error) {
        console.error("Erro ao enviar CPF:", error)
        setError("Não foi possível validar o CPF")
      }
    }
  }


  return (
    <Card className="relative border-2 rounded-lg shadow-sm w-[500px]">
      <CardContent className="p-8">
        <form onSubmit={handleSubmit} className="space-y-6">
          <div className="space-y-2 text-center">
            <h2 className="text-2xl font-bold">Digite seu CPF</h2>
            <p className="text-sm text-gray-500">
              Informar o CPF acumula pontos para trocar por cupons de desconto.
            </p>
          </div>

          {error && <p className="text-center text-sm text-red-600">{error}</p>}

          <div className="relative overflow-visible">
            <Input
              type="text"
              placeholder="000.000.000-00"
              value={formatCpfDigits(cpfDigits)}
              onClick={() => setShowKeypad(true)}
              readOnly
              inputMode="none"
              className="cursor-pointer text-center border-2 rounded-md w-full py-6 text-xl"
            />

            <NumericKeypadDialog
              open={showKeypad}
              onOpenChange={setShowKeypad}
              value={cpfDigits}
              onValueChange={handleChange}
              onConfirm={() => setShowKeypad(false)}
              confirmLabel="Aplicar"
              maxLength={11}
            />
          </div>

          <div className="flex justify-center gap-4">
            <Button type="button" variant="destructive" onClick={onCancel} className="px-6 py-3 rounded-md text-base">
              Cancelar
            </Button>
            <Button
              type="submit"
              className="px-6 py-3 rounded-md text-base"
              disabled={cpfDigits.length !== 11}
            >
              Confirmar
            </Button>
          </div>
        </form>
      </CardContent>
    </Card>
  )
}
