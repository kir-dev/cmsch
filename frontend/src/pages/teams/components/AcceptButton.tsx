import { Button } from '@/components/ui/button'
import { Tooltip, TooltipContent, TooltipProvider, TooltipTrigger } from '@/components/ui/tooltip'
import { Check } from 'lucide-react'

interface AcceptButtonProps {
  onAccept: () => void
}

export function AcceptButton({ onAccept }: AcceptButtonProps) {
  return (
    <TooltipProvider>
      <Tooltip>
        <TooltipTrigger asChild>
          <Button variant="outline" className="border-success text-success hover:bg-success/10" onClick={onAccept}>
            <Check className="h-4 w-4" />
          </Button>
        </TooltipTrigger>
        <TooltipContent>
          <p>Elfogadás</p>
        </TooltipContent>
      </Tooltip>
    </TooltipProvider>
  )
}
