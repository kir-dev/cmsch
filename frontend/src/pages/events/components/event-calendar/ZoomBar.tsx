import { Button } from '@/components/ui/button'
import { useBrandColor } from '@/util/core-functions.util.ts'
import { MinusCircle, PlusCircle } from 'lucide-react'

interface ZoomBarProps {
  scale: number
  incrementScale: () => void
  decrementScale: () => void
}

export function ZoomBar({ scale, incrementScale, decrementScale }: ZoomBarProps) {
  const brandColor = useBrandColor()
  return (
    <div className="flex flex-row justify-center mt-2 items-center space-x-4">
      <Button variant="ghost" size="icon" aria-label="Kicsinyítés" onClick={decrementScale} style={{ color: brandColor }}>
        <MinusCircle className="h-5 w-5" />
      </Button>
      <span>{Math.round(scale * 100)}%</span>
      <Button variant="ghost" size="icon" aria-label="Nagyítás" onClick={incrementScale} style={{ color: brandColor }}>
        <PlusCircle className="h-5 w-5" />
      </Button>
    </div>
  )
}
