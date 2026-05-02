import { fireEvent, render, screen, waitFor } from "@testing-library/react"
import CpfInputPopup from "@/components/cpf-input-popup"

describe("CpfInputPopup", () => {
  it("formats cpf and submits after backend success", async () => {
    const onSubmit = vi.fn()
    const onCancel = vi.fn()
    const fetchMock = vi.fn().mockResolvedValue({
      ok: true,
      json: async () => 0,
    })
    vi.stubGlobal("fetch", fetchMock)

    render(<CpfInputPopup onSubmit={onSubmit} onCancel={onCancel} />)

    fireEvent.change(screen.getByPlaceholderText("000.000.000-00"), {
      target: { value: "52998224725" },
    })

    fireEvent.click(screen.getByRole("button", { name: "Confirmar" }))

    await waitFor(() => expect(fetchMock).toHaveBeenCalledTimes(1))
    expect(fetchMock).toHaveBeenCalledWith(
      "http://localhost:8080/pessoa/verificar-cpf",
      expect.objectContaining({
        method: "POST",
        body: JSON.stringify({ cpf: "52998224725" }),
      })
    )
    await waitFor(() => expect(onSubmit).toHaveBeenCalledWith("529.982.247-25"))
  })

  it("does not submit when cpf is incomplete", async () => {
    const onSubmit = vi.fn()
    const fetchMock = vi.fn()
    vi.stubGlobal("fetch", fetchMock)

    render(<CpfInputPopup onSubmit={onSubmit} onCancel={vi.fn()} />)

    fireEvent.change(screen.getByPlaceholderText("000.000.000-00"), {
      target: { value: "123" },
    })

    expect(screen.getByRole("button", { name: "Confirmar" })).toBeDisabled()
    expect(fetchMock).not.toHaveBeenCalled()
    expect(onSubmit).not.toHaveBeenCalled()
  })

  it("shows backend validation error for invalid cpf", async () => {
    const fetchMock = vi.fn().mockResolvedValue({
      ok: false,
      json: async () => ({ message: "CPF inválido" }),
    })
    vi.stubGlobal("fetch", fetchMock)

    render(<CpfInputPopup onSubmit={vi.fn()} onCancel={vi.fn()} />)

    fireEvent.change(screen.getByPlaceholderText("000.000.000-00"), {
      target: { value: "12345678901" },
    })

    fireEvent.click(screen.getByRole("button", { name: "Confirmar" }))

    await waitFor(() => expect(screen.getByText("CPF inválido")).toBeInTheDocument())
  })
})
