"use client"

import type React from "react"
import { useEffect, useRef, useState } from "react"
import { GripVertical, X } from "lucide-react"

import { Button } from "@/components/ui/button"
import { Card, CardContent } from "@/components/ui/card"
import { appendDigit, removeLastDigit } from "@/lib/numeric-input"

interface NumericKeypadDialogProps {
  leftActionLabel?: string
  confirmLabel: string
  maxLength?: number
  onLeftAction?: () => void
  onConfirm: () => void
  onOpenChange: (open: boolean) => void
  onValueChange: (value: string) => void
  open: boolean
  value: string
}

const KEYPAD_DIGITS = ["1", "2", "3", "4", "5", "6", "7", "8", "9", "0"]

export default function NumericKeypadDialog({
  leftActionLabel,
  confirmLabel,
  maxLength,
  onLeftAction,
  onConfirm,
  onOpenChange,
  onValueChange,
  open,
  value,
}: NumericKeypadDialogProps) {
  const [offset, setOffset] = useState({ x: 0, y: 0 })
  const dragRef = useRef<{
    originX: number
    originY: number
    startX: number
    startY: number
  } | null>(null)

  useEffect(() => {
    if (open) {
      setOffset({ x: 0, y: 0 })
    }
  }, [open])

  if (!open) {
    return null
  }

  const handleDigit = (digit: string) => {
    onValueChange(appendDigit(value, digit, maxLength))
  }

  const handleBackspace = () => {
    onValueChange(removeLastDigit(value))
  }

  const handlePointerDown = (event: React.PointerEvent<HTMLDivElement>) => {
    event.preventDefault()
    dragRef.current = {
      originX: offset.x,
      originY: offset.y,
      startX: event.clientX,
      startY: event.clientY,
    }
    event.currentTarget.setPointerCapture(event.pointerId)
  }

  const handlePointerMove = (event: React.PointerEvent<HTMLDivElement>) => {
    if (!dragRef.current) {
      return
    }

    setOffset({
      x: dragRef.current.originX + (event.clientX - dragRef.current.startX),
      y: dragRef.current.originY + (event.clientY - dragRef.current.startY),
    })
  }

  const handlePointerUp = () => {
    dragRef.current = null
  }

  const hasSecondaryAction = Boolean(onLeftAction && leftActionLabel)

  return (
    <div
      className="absolute left-1/2 top-[calc(100%+12px)] z-40 w-[min(92vw,560px)]"
      style={{ transform: `translate(calc(-50% + ${offset.x}px), ${offset.y}px)` }}
    >
      <Card className="overflow-hidden rounded-2xl border-2 shadow-xl">
        <div
          className="flex items-center justify-between border-b bg-muted/40 px-3 py-2 touch-none cursor-grab active:cursor-grabbing select-none"
          onPointerDown={handlePointerDown}
          onPointerMove={handlePointerMove}
          onPointerUp={handlePointerUp}
          onPointerCancel={handlePointerUp}
        >
          <div className="flex items-center gap-2 text-xs text-muted-foreground">
            <GripVertical className="h-4 w-4" />
          </div>
          <button
            type="button"
            aria-label="Fechar teclado"
            className="rounded-full p-1 text-muted-foreground transition-colors hover:bg-accent hover:text-accent-foreground"
            onClick={() => onOpenChange(false)}
          >
            <X className="h-4 w-4" />
          </button>
        </div>

        <CardContent className="space-y-4 p-4">
          <div className="grid grid-cols-3 gap-3">
            {KEYPAD_DIGITS.slice(0, 9).map((digit) => (
              <Button
                key={digit}
                type="button"
                variant="outline"
                className="h-14 rounded-xl border-2 text-2xl font-semibold touch-manipulation"
                onClick={() => handleDigit(digit)}
              >
                {digit}
              </Button>
            ))}
            <Button
              type="button"
              aria-label="Voltar"
              variant="outline"
              className="h-14 rounded-xl border-2 text-2xl font-semibold touch-manipulation"
              onClick={handleBackspace}
            >
              ⌫
            </Button>
            <Button
              type="button"
              variant="outline"
              className="h-14 rounded-xl border-2 text-2xl font-semibold touch-manipulation"
              onClick={() => handleDigit(KEYPAD_DIGITS[9])}
            >
              0
            </Button>
            <div className="h-14" />
          </div>

          {hasSecondaryAction ? (
            <div className="flex gap-3">
              <Button
                type="button"
                variant="outline"
                className="h-12 flex-1 rounded-xl text-sm"
                onClick={onLeftAction}
              >
                {leftActionLabel}
              </Button>
              <Button type="button" className="h-12 flex-1 rounded-xl text-sm" onClick={onConfirm}>
                {confirmLabel}
              </Button>
            </div>
          ) : (
            <Button type="button" className="h-12 w-full rounded-xl text-sm" onClick={onConfirm}>
              {confirmLabel}
            </Button>
          )}
        </CardContent>
      </Card>
    </div>
  )
}
