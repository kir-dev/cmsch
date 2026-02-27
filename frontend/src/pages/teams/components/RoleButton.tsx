import { Button } from '@/components/ui/button'
import { Tooltip, TooltipContent, TooltipProvider, TooltipTrigger } from '@/components/ui/tooltip'
import { Shield, ShieldOff } from 'lucide-react'

interface RoleButtonProps {
  onRoleChange: () => void
  isAdmin: boolean
}

export function RoleButton({ onRoleChange, isAdmin }: RoleButtonProps) {
  return (
    <TooltipProvider>
      <Tooltip>
        <TooltipTrigger asChild>
          <Button variant="outline" className="border-warning text-warning hover:bg-warning/10" onClick={onRoleChange}>
            {isAdmin ? <ShieldOff className="h-4 w-4" /> : <Shield className="h-4 w-4" />}
          </Button>
        </TooltipTrigger>
        <TooltipContent>
          <p>Jogosultság adása</p>
        </TooltipContent>
      </Tooltip>
    </TooltipProvider>
  )
}
