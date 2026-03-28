import type { Menu } from '@/api/contexts/config/types'
import { Collapsible, CollapsibleContent, CollapsibleTrigger } from '@/components/ui/collapsible'
import { ChevronDown } from 'lucide-react'
import { useState } from 'react'
import { Link } from 'react-router'
import LinkComponent from '../LinkComponent'

type Props = {
  menu: Menu
  onNavigate?: () => void
}

export const NavItemWithChildren = ({ menu: { external, url, children, name }, onNavigate }: Props) => {
  const [isOpen, setIsOpen] = useState(false)

  return (
    <Collapsible open={isOpen} onOpenChange={setIsOpen}>
      <CollapsibleTrigger asChild>
        <div className="mt-0">
          <LinkComponent url={url} external={external}>
            <div className="flex py-2 justify-between items-center hover:no-underline cursor-pointer">
              <span className="font-medium text-foreground">{name}</span>
              <ChevronDown className={`h-4 w-4 transition-transform duration-200 ${isOpen ? 'rotate-180' : ''}`} />
            </div>
          </LinkComponent>
        </div>
      </CollapsibleTrigger>

      <CollapsibleContent>
        <div className="flex flex-col pl-4 border-l border-solid border-border items-start">
          {children.map((child) => (
            <Link key={child.url} to={child.url || '#'} className="w-full" onClick={onNavigate}>
              <span className="block py-2">{child.name}</span>
            </Link>
          ))}
        </div>
      </CollapsibleContent>
    </Collapsible>
  )
}
