"use client"

import type React from "react"
import { useState } from "react"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Card, CardContent } from "@/components/ui/card"
import { X } from "lucide-react"

interface CpfScreenProps {
  onSubmit: (cpf: string, pontos: number) => void
  onCancel?: () => void
}

export default function CpfScreen({ onSubmit, onCancel }: CpfScreenProps) {
  const [cpf, setCpf] = useState("")

  const formatCpf = (value: string) => {
    const numericValue = value.replace(/\D/g, "")
    if (numericValue.length <= 3) {
      return numericValue
    } else if (numericValue.length <= 6) {
      return `${numericValue.slice(0, 3)}.${numericValue.slice(3)}`
    } else if (numericValue.length <= 9) {
      return `${numericValue.slice(0, 3)}.${numericValue.slice(3, 6)}.${numericValue.slice(6)}`
    } else {
      return `${numericValue.slice(0, 3)}.${numericValue.slice(3, 6)}.${numericValue.slice(6, 9)}-${numericValue.slice(9, 11)}`
    }
  }

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setCpf(formatCpf(e.target.value))
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    const numericCpf = cpf.replace(/\D/g, "")
    if (numericCpf.length === 11) {
      try {
        const response = await fetch("http://localhost:8080/pessoa/verificar-cpf", {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({ cpf: numericCpf }),
        })

        if (response.ok) {
          const data = await response.json()
          const pontos = data.pontos || 0
          onSubmit(cpf, pontos)
        } else {
          console.error("Erro na requisição:", response.statusText)
        }
      } catch (error) {
        console.error("Erro ao enviar CPF:", error)
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
          <form onSubmit={handleSubmit} className="space-y-6">
            <div className="space-y-3 text-center">
              <h2 className="text-2xl font-bold">Digite seu CPF</h2>
              <p className="text-gray-500 text-base">Por favor, insira seu CPF para compras com desconto.</p>
            </div>

            <Input
                type="text"
                placeholder="000.000.000-00"
                value={cpf}
                onChange={handleChange}
                maxLength={14}
                style={{ fontSize: "1.5rem", padding: "1.5rem 0" }}
                className="text-center border-2 rounded-md w-full"
            />

            <div className="flex flex-col gap-3">
              <Button
                  type="submit"
                  className="w-full bg-[#2d5d3d] hover:bg-[#224731] text-white py-3 text-base rounded-md"
                  disabled={cpf.replace(/\D/g, "").length !== 11}
              >
                Continuar
              </Button>

              <Button
                  type="button"
                  variant="outline"
                  onClick={handleContinueWithoutCpf}
                  className="w-full border-[#2d5d3d] text-[#2d5d3d] hover:bg-[#f0f7f3] py-3 text-base rounded-md"
              >
                Continuar sem CPF
              </Button>
            </div>
          </form>
        </CardContent>
      </Card>
  )
}
