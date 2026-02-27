import type { Menu } from '@/api/contexts/config/types'
import { cn } from '@/lib/utils'
import { ChevronDown } from 'lucide-react'
import { useState } from 'react'
import { Link } from 'react-router'
import LinkComponent from '../LinkComponent'

type Props = {
  menu: Menu
}

export const NavItemWithChildren = ({ menu: { external, url, children, name } }: Props) => {
  const [isOpen, setIsOpen] = useState(false)
  const onToggle = () => setIsOpen(!isOpen)

  return (
    <div className="mt-0" onClick={onToggle}>
      <LinkComponent url={url} external={external}>
        <div className="flex py-2 justify-between items-center hover:no-underline cursor-pointer">
          <span className="font-medium text-gray-800 dark:text-gray-200">{name}</span>
          <ChevronDown className={cn('h-4 w-4 transition-transform duration-200', isOpen && 'rotate-180')} />
        </div>
      </LinkComponent>

      {isOpen && (
        <div className="flex flex-col pl-4 border-l border-solid border-gray-200 dark:border-gray-800 items-start">
          {children.map((child) => (
            <Link key={child.url} to={child.url || '#'} className="navitem w-full" onClick={(e) => e.stopPropagation()}>
              <span className="block py-2">{child.name}</span>
            </Link>
          ))}
        </div>
      )}
    </div>
  )
}
