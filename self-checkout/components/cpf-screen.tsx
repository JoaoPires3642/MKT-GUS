"use client"

import type React from "react"
import { useState } from "react"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Card, CardContent } from "@/components/ui/card"
import { X } from "lucide-react"
import NumericKeypadDialog from "@/components/numeric-keypad-dialog"
import { formatCpfDigits, onlyDigits } from "@/lib/numeric-input"

interface CpfScreenProps {
  onSubmit: (cpf: string, pontos: number) => void
  onCancel?: () => void
}

export default function CpfScreen({ onSubmit, onCancel }: CpfScreenProps) {
  const [cpfDigits, setCpfDigits] = useState("")
  const [error, setError] = useState<string | null>(null)
  const [showKeypad, setShowKeypad] = useState(false)

  const handleChange = (value: string) => {
    setCpfDigits(onlyDigits(value).slice(0, 11))
    if (error) {
      setError(null)
    }
  }

  const handleConfirm = async () => {
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
          const data = await response.json()
          const pontos = data.pontos || 0
          onSubmit(cpfDigits, pontos)
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

  const handleContinueWithoutCpf = () => {
    onSubmit("", 0)
  }

  return (
      <Card className="relative border-2 rounded-lg shadow-sm w-[500px]">
        {onCancel && (
            <Button
                variant="ghost"
                size="icon"
                className="absolute right-2 top-2 rounded-full h-8 w-8 p-0 z-10"
                onClick={onCancel}
            >
              <X className="h-5 w-5" />
            </Button>
        )}

        <CardContent className="p-8">
          <div className="space-y-6">
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
                onLeftAction={handleContinueWithoutCpf}
                leftActionLabel="Continuar sem CPF"
                onConfirm={handleConfirm}
                confirmLabel="Aplicar"
                maxLength={11}
              />
            </div>

            {!showKeypad && (
              <Button
                type="button"
                variant="outline"
                onClick={handleContinueWithoutCpf}
                className="w-full border-primary text-primary hover:bg-primary/10 py-3 text-base rounded-md"
              >
                Continuar sem CPF
              </Button>
            )}
          </div>
        </CardContent>
      </Card>
  )
}
