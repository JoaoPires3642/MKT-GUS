import { fireEvent, render, screen, waitFor } from "@testing-library/react"
import MyPointsPopup from "@/components/my-points-popup"

describe("MyPointsPopup", () => {
  it("loads coupons and applies selected coupon", async () => {
    const onApplyCoupon = vi.fn()
    const couponPayload = [
      {
        id: 1,
        name: "Cupom 10%",
        description: "Desconto teste",
        percentageDiscount: true,
        discountValue: 10,
        cost: 100,
        minimumPurchase: 20,
        maximumDiscount: 30,
      },
    ]
    vi.stubGlobal(
      "fetch",
      vi.fn().mockResolvedValue({
        ok: true,
        json: async () => couponPayload,
      })
    )

    render(
      <MyPointsPopup
        onClose={vi.fn()}
        pointsBalance={200}
        onApplyCoupon={onApplyCoupon}
        appliedCoupon={null}
        subtotal={50}
      />
    )

    await waitFor(() => expect(screen.getByText("Cupom 10%")).toBeInTheDocument())
    fireEvent.click(screen.getByText("Cupom 10%"))
    fireEvent.click(screen.getByRole("button", { name: "Aplicar Cupom" }))

    await waitFor(() => expect(onApplyCoupon).toHaveBeenCalledTimes(1))
    expect(onApplyCoupon).toHaveBeenCalledWith(
      expect.objectContaining({
        id: "1",
        name: "Cupom 10%",
        pointsCost: 100,
      })
    )
  })

  it("blocks coupon selection when points are insufficient", async () => {
    vi.stubGlobal(
      "fetch",
      vi.fn().mockResolvedValue({
        ok: true,
        json: async () => [
          {
            id: 1,
            name: "Cupom caro",
            description: "Sem saldo",
            percentageDiscount: false,
            discountValue: 20,
            cost: 500,
          },
        ],
      })
    )

    render(
      <MyPointsPopup
        onClose={vi.fn()}
        pointsBalance={100}
        onApplyCoupon={vi.fn()}
        appliedCoupon={null}
        subtotal={50}
      />
    )

    await waitFor(() => expect(screen.getByText("Cupom caro")).toBeInTheDocument())
    expect(screen.getByRole("button", { name: "Aplicar Cupom" })).toBeDisabled()
  })
})
