export function onlyDigits(value: string) {
  return value.replace(/\D/g, "")
}

export function appendDigit(value: string, digit: string, maxLength?: number) {
  if (!/^\d$/.test(digit)) {
    return value
  }

  if (maxLength && value.length >= maxLength) {
    return value
  }

  return `${value}${digit}`
}

export function removeLastDigit(value: string) {
  return value.slice(0, -1)
}

export function formatCpfDigits(value: string) {
  const digits = onlyDigits(value).slice(0, 11)

  if (digits.length <= 3) {
    return digits
  }

  if (digits.length <= 6) {
    return `${digits.slice(0, 3)}.${digits.slice(3)}`
  }

  if (digits.length <= 9) {
    return `${digits.slice(0, 3)}.${digits.slice(3, 6)}.${digits.slice(6)}`
  }

  return `${digits.slice(0, 3)}.${digits.slice(3, 6)}.${digits.slice(6, 9)}-${digits.slice(9, 11)}`
}
