export function getToastTitle(type?: string) {
  switch (type) {
    case 'success':
      return 'Siker'
    case 'error':
      return 'Hiba'
    case 'warning':
      return 'Figyelmeztetés'
    default:
      return 'Információ'
  }
}
