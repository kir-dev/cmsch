import { Button } from '@/components/ui/button'
import { Tooltip, TooltipContent, TooltipProvider, TooltipTrigger } from '@/components/ui/tooltip'
import { Crown } from 'lucide-react'

interface LeaderButtonProps {
  onPromoteLeadership: () => void
}

export function LeaderButton({ onPromoteLeadership }: LeaderButtonProps) {
  return (
    <TooltipProvider>
      <Tooltip>
        <TooltipTrigger asChild>
          <Button variant="outline" className="border-info text-info hover:bg-info/10" onClick={onPromoteLeadership}>
            <Crown className="h-4 w-4" />
          </Button>
        </TooltipTrigger>
        <TooltipContent>
          <p>Csapatkapitánnyá tevés</p>
        </TooltipContent>
      </Tooltip>
    </TooltipProvider>
  )
}
