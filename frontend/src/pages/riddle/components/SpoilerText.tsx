import { cn } from '@/lib/utils'
import { useState } from 'react'

type Props = {
  text: string
}

export const SpoilerText = ({ text }: Props) => {
  const [isOpen, setIsOpen] = useState(false)

  return (
    <span
      onClick={() => setIsOpen(!isOpen)}
      className={cn('cursor-pointer transition-colors', isOpen ? '' : 'bg-foreground text-foreground select-none')}
    >
      {text}
    </span>
  )
}
