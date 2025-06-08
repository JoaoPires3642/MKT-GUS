"use client"

import { Button } from "@/components/ui/button"
import { ArrowRight } from "lucide-react"
import Image from "next/image"

interface WelcomeScreenProps {
  onStartShopping: () => void
}

export default function WelcomeScreen({ onStartShopping }: WelcomeScreenProps) {
  return (
    <div className="flex flex-col items-center justify-center w-[600px] space-y-5 text-center">
      <div className="relative -mb-6">
        <Image src="/images/logo.jpg" alt="Logo" width={500} height={500} priority />
      </div>

      <h1 className="text-4xl font-bold">Bem-vindo ao Auto Atendimento</h1>

      <p className="text-gray-600 text-xl max-w-[500px]">
        Escaneie seus produtos e realize o pagamento de forma rápida e segura.
      </p>

      <Button
        onClick={onStartShopping}
        className="bg-[#2d5d3d] hover:bg-[#224731] text-white px-8 py-6 rounded-md flex items-center gap-3 text-xl mt-4"
      >
        Iniciar Compras <ArrowRight size={24} />
      </Button>
    </div>
  )
}
