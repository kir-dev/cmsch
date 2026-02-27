import { calculatePosition } from './utils'

interface CurrentDateBarProps {
  minTimestamp: number
  maxTimestamp: number
}

export function CurrentDateBar({ maxTimestamp, minTimestamp }: CurrentDateBarProps) {
  // eslint-disable-next-line react-hooks/purity
  const now = Date.now()
  if (now < minTimestamp || now > maxTimestamp) return null
  const position = calculatePosition(minTimestamp, maxTimestamp, now)
  return <div className="z-10 absolute left-0 right-0 border-t-2 border-danger rounded-full" style={{ top: position + '%' }} />
}
