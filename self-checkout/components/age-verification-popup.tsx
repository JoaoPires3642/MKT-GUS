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
import NumericKeypadDialog from "@/components/numeric-keypad-dialog";
import { onlyDigits } from "@/lib/numeric-input";

interface AgeVerificationPopupProps {
  onConfirm: (employeeId: string) => void;
  onCancel: () => void;
}

export default function AgeVerificationPopup({ onConfirm, onCancel }: AgeVerificationPopupProps) {
  const [employeeId, setEmployeeId] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [showInvalidPopup, setShowInvalidPopup] = useState(false);
  const [showKeypad, setShowKeypad] = useState(false);

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
        onConfirm(employeeId);
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

              <div className="relative overflow-visible">
                <Input
                    type="text"
                    placeholder="ID FUNCIONÁRIO"
                    value={employeeId}
                    onClick={() => setShowKeypad(true)}
                    readOnly
                    inputMode="none"
                    className="cursor-pointer py-5 text-base border-2 mt-4"
                    disabled={isLoading}
                />

                <NumericKeypadDialog
                    open={showKeypad}
                    onOpenChange={setShowKeypad}
                    value={employeeId}
                    onValueChange={(value) => {
                      setEmployeeId(onlyDigits(value))
                      if (error) {
                        setError(null)
                      }
                    }}
                    onConfirm={() => setShowKeypad(false)}
                    confirmLabel="Aplicar"
                    maxLength={12}
                />
              </div>

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
                    className="px-6 py-3 rounded-md text-base"
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
              <Button onClick={() => setShowInvalidPopup(false)}>
                Fechar
              </Button>
            </DialogFooter>
          </DialogContent>
        </Dialog>
      </>
  );
}
