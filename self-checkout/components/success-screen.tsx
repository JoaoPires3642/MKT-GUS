"use client"

import { Button } from "@/components/ui/button"
import Image from "next/image"

interface SuccessScreenProps {
  onNewPurchase: () => void
}

export default function SuccessScreen({ onNewPurchase }: SuccessScreenProps) {
  return (
    <div className="flex flex-col items-center justify-center w-[600px] space-y-5 text-center">
      <div className="relative -mb-6">
        <Image src="/images/logo.jpg" alt="Logo" width={500} height={500} priority />
      </div>

      <h1 className="text-3xl font-bold">Compra Finalizada com Sucesso!</h1>

      <p className="text-gray-600 text-xl">Obrigado Volte Sempre.</p>

      <Button
        onClick={onNewPurchase}
        className="bg-[#2d5d3d] hover:bg-[#224731] text-white px-8 py-4 rounded-md text-lg mt-4"
      >
        Nova Compra
      </Button>
    </div>
  )
}
