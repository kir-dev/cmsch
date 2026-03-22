import { Button } from '@/components/ui/button'
import { Tooltip, TooltipContent, TooltipProvider, TooltipTrigger } from '@/components/ui/tooltip'
import { Trash2 } from 'lucide-react'

interface DeleteButtonProps {
  onDelete: () => void
}

export function DeleteButton({ onDelete }: DeleteButtonProps) {
  return (
    <TooltipProvider>
      <Tooltip>
        <TooltipTrigger asChild>
          <Button variant="outline" className="border-danger text-danger hover:bg-danger/10" onClick={onDelete}>
            <Trash2 className="h-4 w-4" />
          </Button>
        </TooltipTrigger>
        <TooltipContent>
          <p>Törlés</p>
        </TooltipContent>
      </Tooltip>
    </TooltipProvider>
  )
}
