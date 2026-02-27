import { PulsingDot } from '@/common-components/PulsingDot'
import { ChevronDown, ChevronUp } from 'lucide-react'

type CardListItemProps = {
  title: string
  open: boolean
  toggle: () => void
  showPulsingDot?: boolean
  pulsingDotColor?: string
}

export const CardListItem = ({ title, open, toggle, showPulsingDot, pulsingDotColor }: CardListItemProps) => {
  return (
    <div
      onClick={toggle}
      className="mt-2 cursor-pointer rounded-lg bg-secondary text-secondary-foreground border p-4 transition-colors hover:bg-secondary/80"
    >
      <div className="flex items-center">
        <h3 className="max-w-full text-lg font-bold">{title}</h3>
        <div className="flex-grow" />
        <div className="flex items-center gap-2">
          {showPulsingDot && <PulsingDot color={pulsingDotColor} />}
          {open ? <ChevronUp className="h-5 w-5 md:h-8 md:w-8" /> : <ChevronDown className="h-5 w-5 md:h-8 md:w-8" />}
        </div>
      </div>
    </div>
  )
}
