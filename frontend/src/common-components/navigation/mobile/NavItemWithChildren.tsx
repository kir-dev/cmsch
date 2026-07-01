import type { Menu } from '@/api/contexts/config/types'
import { Collapsible, CollapsibleContent, CollapsibleTrigger } from '@/components/ui/collapsible'
import { ChevronDown } from 'lucide-react'
import { useState } from 'react'
import { Link } from 'react-router'

type Props = {
  menu: Menu
  onNavigate?: () => void
}

export const NavItemWithChildren = ({ menu: { children, name }, onNavigate }: Props) => {
  const [isOpen, setIsOpen] = useState(false)
  return (
    <Collapsible open={isOpen} onOpenChange={setIsOpen}>
      <CollapsibleTrigger asChild>
        <div className="flex py-2 justify-between items-center hover:no-underline cursor-pointer">
          <span className="font-medium text-foreground">{name}</span>
          <ChevronDown className={`h-4 w-4 transition-transform duration-200 ${isOpen ? 'rotate-180' : ''}`} />
        </div>
      </CollapsibleTrigger>

      <CollapsibleContent>
        <div className="flex flex-col justify-center">
          {children.map((child) => (
            <Link key={child.url} to={child.url || '#'} className="w-full text-center" onClick={onNavigate}>
              <span className="block py-2">{child.name}</span>
            </Link>
          ))}
        </div>
      </CollapsibleContent>
    </Collapsible>
  )
}
