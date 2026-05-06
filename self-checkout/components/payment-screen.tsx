"use client"

import { useState } from "react";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { Check, CreditCard, QrCode } from "lucide-react";
import type { Coupon } from "@/lib/types";

interface PaymentScreenProps {
  onConfirm: () => void;
  onBack: () => void;
  cpf?: string; // CPF é opcional
  appliedCoupon: Coupon | null;
}

export default function PaymentScreen({ onConfirm, onBack, cpf, appliedCoupon }: PaymentScreenProps) {
  const [selectedPaymentMethod, setSelectedPaymentMethod] = useState<"card" | "pix" | null>(null);

  const handleSelectPaymentMethod = (method: "card" | "pix") => {
    setSelectedPaymentMethod(method);
  };

  return (
      <div className="w-[800px] flex flex-col justify-center space-y-6 p-6">
        <h2 className="text-2xl font-bold mb-4">Escolha o método de pagamento</h2>

        <div className="space-y-4">
          <Card
              className={`border-2 cursor-pointer transition-all ${
                  selectedPaymentMethod === "card" ? "border-primary bg-primary/10" : "hover:border-primary"
              }`}
              onClick={() => handleSelectPaymentMethod("card")}
          >
            <CardContent className="p-6 flex justify-between items-center">
              <div className="flex items-center gap-3">
                <CreditCard className="h-6 w-6 text-primary" />
                <h3 className="text-lg font-medium">Cartão de Crédito/Débito</h3>
              </div>
              {selectedPaymentMethod === "card" && (
                  <div className="w-6 h-6 rounded-full bg-primary flex items-center justify-center">
                    <Check className="h-4 w-4 text-white" />
                  </div>
              )}
            </CardContent>
          </Card>

          <Card
              className={`border-2 cursor-pointer transition-all ${
                  selectedPaymentMethod === "pix" ? "border-primary bg-primary/10" : "hover:border-primary"
              }`}
              onClick={() => handleSelectPaymentMethod("pix")}
          >
            <CardContent className="p-6 flex justify-between items-center">
              <div className="flex items-center gap-3">
                <QrCode className="h-6 w-6 text-primary" />
                <h3 className="text-lg font-medium">PIX</h3>
              </div>
              {selectedPaymentMethod === "pix" && (
                  <div className="w-6 h-6 rounded-full bg-primary flex items-center justify-center">
                    <Check className="h-4 w-4 text-white" />
                  </div>
              )}
            </CardContent>
          </Card>
        </div>

        <div className="flex justify-between gap-4 pt-4 mt-6">
          <Button
              variant="outline"
              onClick={onBack}
              className="flex-1 py-4 bg-gray-200 hover:bg-gray-300 border-0 text-base"
          >
            Voltar
          </Button>

          <Button
              onClick={onConfirm}
              className="flex-1 py-4 text-base"
              disabled={selectedPaymentMethod === null}
          >
            Confirmar Pagamento
          </Button>
        </div>
      </div>
  );
}
