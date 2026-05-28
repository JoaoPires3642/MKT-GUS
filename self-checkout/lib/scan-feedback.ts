type AudioContextConstructor = typeof AudioContext

type WindowWithWebkitAudioContext = Window & {
  webkitAudioContext?: AudioContextConstructor
}

export function playProductDetectedBeep() {
  if (typeof window === "undefined") {
    return
  }

  const AudioContextClass = window.AudioContext ?? (window as WindowWithWebkitAudioContext).webkitAudioContext
  if (!AudioContextClass) {
    return
  }

  try {
    const audioContext = new AudioContextClass()
    const oscillator = audioContext.createOscillator()
    const gain = audioContext.createGain()

    oscillator.type = "sine"
    oscillator.frequency.setValueAtTime(880, audioContext.currentTime)
    gain.gain.setValueAtTime(0.001, audioContext.currentTime)
    gain.gain.exponentialRampToValueAtTime(0.2, audioContext.currentTime + 0.01)
    gain.gain.exponentialRampToValueAtTime(0.001, audioContext.currentTime + 0.14)

    oscillator.connect(gain)
    gain.connect(audioContext.destination)
    oscillator.start()
    oscillator.stop(audioContext.currentTime + 0.15)
    oscillator.onended = () => audioContext.close()
  } catch {
    // O som é apenas feedback auxiliar; não deve quebrar a leitura do produto.
  }
}
