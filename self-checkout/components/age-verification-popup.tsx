"use client";

import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from "@/components/ui/dialog";
import { useState } from "react";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import axios from "axios";

interface AgeVerificationPopupProps {
  onConfirm: () => void;
  onCancel: () => void;
}

export default function AgeVerificationPopup({ onConfirm, onCancel }: AgeVerificationPopupProps) {
  const [employeeId, setEmployeeId] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [showInvalidPopup, setShowInvalidPopup] = useState(false);

  const handleConfirm = async () => {
    if (!employeeId.trim()) {
      setError("Por favor, insira uma matrícula válida.");
      return;
    }

    setIsLoading(true);
    setError(null);

    try {
      const response = await axios.post("http://localhost:8080/api/funcionarios/verificar-matricula", {
        matricula: employeeId,
      });

      if (response.data.valid) {
        onConfirm();
      } else {
        setError(response.data.message || "Matrícula inválida.");
        setShowInvalidPopup(true);
      }
    } catch (error) {
      console.error("Erro ao verificar matrícula:", error);
      setError("Erro ao verificar matrícula. Tente novamente.");
      setShowInvalidPopup(true);
    } finally {
      setIsLoading(false);
    }
  };

  return (
      <>
        <Card className="relative border-2 rounded-lg shadow-sm w-[550px]">
          <CardContent className="p-8">
            <div className="space-y-6">
              <h2 className="text-2xl font-bold">Confirmação de Idade</h2>

              <p className="text-gray-500 text-base">
                Esse produto é destinado apenas para pessoas maiores de 18 anos. Por favor, chame um funcionário para
                confirmar a compra.
              </p>

              <Input
                  type="text"
                  placeholder="ID FUNCIONÁRIO"
                  value={employeeId}
                  onChange={(e) => setEmployeeId(e.target.value)}
                  className="py-5 text-base border-2 mt-4"
                  disabled={isLoading}
              />

              {error && <p className="text-red-500 text-sm">{error}</p>}

              <div className="flex justify-end gap-4 pt-2">
                <Button
                    onClick={onCancel}
                    variant="destructive"
                    className="px-6 py-3 rounded-md text-base"
                    disabled={isLoading}
                >
                  Cancelar
                </Button>
                <Button
                    onClick={handleConfirm}
                    className="bg-[#2d5d3d] hover:bg-[#224731] text-white px-6 py-3 rounded-md text-base"
                    disabled={isLoading || !employeeId.trim()}
                >
                  {isLoading ? "Verificando..." : "Confirmar"}
                </Button>
              </div>
            </div>
          </CardContent>
        </Card>

        <Dialog open={showInvalidPopup} onOpenChange={setShowInvalidPopup}>
          <DialogContent>
            <DialogHeader>
              <DialogTitle>Funcionário Inválido</DialogTitle>
              <DialogDescription>
                O ID do funcionário informado não foi encontrado. Por favor, verifique o ID e tente novamente.
              </DialogDescription>
            </DialogHeader>
            <DialogFooter>
              <Button
                  onClick={() => setShowInvalidPopup(false)}
                  className="bg-[#2d5d3d] hover:bg-[#224731] text-white"
              >
                Fechar
              </Button>
            </DialogFooter>
          </DialogContent>
        </Dialog>
      </>
  );
}