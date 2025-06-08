"use client"

import type React from "react"
import { useState } from "react"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Card, CardContent } from "@/components/ui/card"

interface CpfInputPopupProps {
  onSubmit: (cpf: string) => void
  onCancel: () => void
}

export default function CpfInputPopup({ onSubmit, onCancel }: CpfInputPopupProps) {
  const [cpf, setCpf] = useState("")

  const formatCpf = (value: string) => {
    // Remove non-numeric characters
    const numericValue = value.replace(/\D/g, "")

    // Apply CPF mask (000.000.000-00)
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
    const formattedValue = formatCpf(e.target.value)
    setCpf(formattedValue)
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
          body: JSON.stringify({ cpf: numericCpf }), // Alteração aqui
        })

        if (response.ok) {
          const result = await response.json()
          console.log(result)  // Aqui você vê a resposta completa
          onSubmit(cpf) // ou tratar a resposta
        } else {
          console.error("Erro na requisição:", response.statusText)
        }
      } catch (error) {
        console.error("Erro ao enviar CPF:", error)
      }
    }
  }


  return (
    <Card className="relative border-2 rounded-lg shadow-sm w-[500px]">
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
              style={{ fontSize: "1.5rem", padding: "1.5rem 0" }} // força essa porra
              className="text-center border-2 rounded-md w-full"
          />

          <div className="flex justify-center gap-4">
            <Button type="button" variant="destructive" onClick={onCancel} className="px-6 py-3 rounded-md text-base">
              Cancelar
            </Button>
            <Button
              type="submit"
              className="bg-[#2d5d3d] hover:bg-[#224731] text-white px-6 py-3 rounded-md text-base"
              disabled={cpf.replace(/\D/g, "").length !== 11}
            >
              Confirmar
            </Button>
          </div>
        </form>
      </CardContent>
    </Card>
  )
}
