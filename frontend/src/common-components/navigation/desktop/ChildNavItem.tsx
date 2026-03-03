import type { Menu } from '@/api/contexts/config/types'
import { ChevronRight } from 'lucide-react'
import LinkComponent from '../LinkComponent'

export const ChildNavItem = ({ menu }: { menu: Menu }) => {
  return (
    <LinkComponent url={menu.url || '#'} external={menu.external}>
      <div className="group block p-2 rounded-md transition-colors hover:bg-accent">
        <div className="flex flex-row items-center">
          <div>
            <span className="font-medium transition-colors group-hover:text-primary">{menu.name}</span>
          </div>
          <div
            className={
              'flex flex-1 justify-end items-center transition-all opacity-0 ' +
              '-translate-x-2 group-hover:opacity-100 group-hover:translate-x-0'
            }
          >
            <ChevronRight className="h-5 w-5 text-primary" />
          </div>
        </div>
      </div>
    </LinkComponent>
  )
}
