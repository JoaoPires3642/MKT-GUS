/**
 * Returns a masked CPF string in the format ***.XXX.XXX-**
 * Accepts formatted (529.982.247-25) or digits-only (52998224725).
 * Returns "***" for null, undefined, or invalid input.
 */
export function maskCpf(cpf: string | null | undefined): string {
  if (!cpf) return "***"
  const digits = cpf.replace(/\D/g, "")
  if (digits.length !== 11) return "***"
  return `***.${digits.slice(3, 6)}.${digits.slice(6, 9)}-**`
}