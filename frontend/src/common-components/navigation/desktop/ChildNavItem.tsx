import type { Menu } from '@/api/contexts/config/types'
import { useBrandColor } from '@/util/core-functions.util.ts'
import { ChevronRight } from 'lucide-react'
import LinkComponent from '../LinkComponent'

type Props = {
  menu: Menu
}

export const ChildNavItem = ({ menu }: Props) => {
  const color = useBrandColor()
  return (
    <LinkComponent url={menu.url || '#'} external={menu.external}>
      <div className="group block p-2 rounded-md transition-colors hover:bg-accent">
        <div className="flex flex-row items-center">
          <div>
            <span
              className="font-medium transition-colors group-hover:text-[var(--hover-color)]"
              style={{ '--hover-color': color } as React.CSSProperties}
            >
              {menu.name}
            </span>
          </div>
          <div
            className={
              'flex flex-1 justify-end items-center transition-all opacity-0 ' +
              '-translate-x-2 group-hover:opacity-100 group-hover:translate-x-0'
            }
          >
            <ChevronRight className="h-5 w-5" style={{ color: color }} />
          </div>
        </div>
      </div>
    </LinkComponent>
  )
}
