"use client";

import { useState, useEffect } from "react";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { Ticket } from "lucide-react";
import type { Coupon } from "@/lib/types";

interface MyPointsPopupProps {
  onClose: () => void;
  pointsBalance: number;
  onApplyCoupon: (coupon: Coupon) => void;
  appliedCoupon: Coupon | null;
  subtotal: number;
}

export default function MyPointsPopup({ onClose, pointsBalance, onApplyCoupon, appliedCoupon, subtotal }: MyPointsPopupProps) {
  const [selectedCoupon, setSelectedCoupon] = useState<Coupon | null>(appliedCoupon);
  const [availableCoupons, setAvailableCoupons] = useState<Coupon[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    async function fetchCoupons() {
      try {
        setIsLoading(true);
        const response = await fetch("http://localhost:8080/api/cupons");
        if (!response.ok) {
          throw new Error("Erro ao buscar cupons");
        }
        const data = await response.json();

        const mappedCoupons: Coupon[] = data.map((cupom: any) => ({
          id: cupom.id.toString(),
          name: cupom.nome,
          description: cupom.descricao,
          type: cupom.descontoEmPorcentual ? "percentage" : "fixed",
          value: cupom.valorDesconto,
          pointsCost: cupom.custo,
          minPurchase: cupom.minPurchase || undefined,
          maxDiscount: cupom.maxDiscount || undefined,
        }));

        setAvailableCoupons(mappedCoupons);
      } catch (err) {
        setError("Não foi possível carregar os cupons. Tente novamente.");
        console.error(err);
      } finally {
        setIsLoading(false);
      }
    }

    fetchCoupons();
  }, []);

  const handleSelectCoupon = (coupon: Coupon) => {
    if (pointsBalance < coupon.pointsCost) {
      setError("Saldo de pontos insuficiente para este cupom.");
    } else if (coupon.minPurchase && subtotal < coupon.minPurchase) {
      setError(`O valor da compra (R$${subtotal.toFixed(2)}) é menor que o mínimo necessário (R$${coupon.minPurchase.toFixed(2)}).`);
    } else {
      setSelectedCoupon(coupon);
      setError(null);
    }
  };

  const handleApplyCoupon = () => {
    if (!selectedCoupon) {
      setError("Nenhum cupom selecionado.");
    } else if (selectedCoupon.minPurchase && subtotal < selectedCoupon.minPurchase) {
      setError(`O valor da compra (R$${subtotal.toFixed(2)}) é menor que o mínimo necessário (R$${selectedCoupon.minPurchase.toFixed(2)}).`);
    } else {
      onApplyCoupon(selectedCoupon);
      setError(null);
      onClose();
    }
  };

  return (
      <Card className="relative border-2 rounded-lg shadow-sm w-[600px] max-h-[80vh] flex flex-col">
        {/* Cabeçalho fixo */}
        <div className="sticky top-0 bg-white z-10 p-8 border-b">
          <div className="text-center">
            <h2 className="text-2xl font-bold">Meus Pontos: {pointsBalance}pts!</h2>
            <p className="text-gray-500 mt-2">Selecione um cupom para resgatar com seus pontos</p>
          </div>
        </div>

        {/* Área de rolagem para cupons */}
        <CardContent className="flex-1 overflow-y-auto p-8">
          {isLoading && <p className="text-center">Carregando cupons...</p>}
          {error && <p className="text-center text-red-500">{error}</p>}

          {!isLoading && !error && (
              <div className="grid grid-cols-2 gap-4">
                {availableCoupons.map((coupon) => {
                  const isBlocked = pointsBalance < coupon.pointsCost || (coupon.minPurchase && subtotal < coupon.minPurchase);
                  return (
                      <div
                          key={coupon.id}
                          className={`border-2 rounded-lg p-4 transition-all ${
                              selectedCoupon?.id === coupon.id
                                  ? "border-[#2d5d3d] bg-[#f0f7f3]"
                                  : isBlocked
                                      ? "opacity-50 cursor-not-allowed"
                                      : "hover:border-[#2d5d3d] cursor-pointer"
                          }`}
                          onClick={() => !isBlocked && handleSelectCoupon(coupon)}
                      >
                        <div className="flex items-start gap-3">
                          <div className="bg-[#f0f7f3] p-2 rounded-md">
                            <Ticket className="h-6 w-6 text-[#2d5d3d]" />
                          </div>
                          <div className="flex-1">
                            <h3 className="font-medium">{coupon.name}</h3>
                            <p className="text-sm text-gray-500">{coupon.description}</p>
                            <div className="mt-2 text-sm font-medium text-[#2d5d3d]">{coupon.pointsCost} pontos</div>
                            {coupon.type === "percentage" && coupon.maxDiscount && (
                                <p className="text-sm text-gray-500">
                                  Desconto máximo: R${coupon.maxDiscount.toFixed(2)}
                                </p>
                            )}
                            {coupon.minPurchase && (
                                <p className="text-sm text-gray-500">
                                  Compra mínima: R${coupon.minPurchase.toFixed(2)}
                                </p>
                            )}

                          </div>
                        </div>
                      </div>
                  );
                })}
              </div>
          )}
        </CardContent>

        {/* Botões fixos na parte inferior */}
        <div className="sticky bottom-0 bg-white z-10 p-8 border-t">
          <div className="flex justify-between gap-4">
            <Button variant="destructive" onClick={onClose} className="flex-1 py-3 text-base">
              Cancelar
            </Button>
            <Button
                onClick={handleApplyCoupon}
                className="flex-1 bg-[#2d5d3d] hover:bg-[#224731] text-white py-3"
                disabled={!selectedCoupon}
            >
              Aplicar Cupom
            </Button>
          </div>
        </div>
      </Card>
  );
}