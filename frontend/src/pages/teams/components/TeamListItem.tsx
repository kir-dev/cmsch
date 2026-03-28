import { AbsolutePaths } from '@/util/paths'
import { ChevronRight } from 'lucide-react'
import { Link } from 'react-router'

import { cn } from '@/lib/utils'
import type { TeamListItemView } from '@/util/views/team.view'
import TeamLabel from './TeamLabel.tsx'

type TeamListItemProps = {
  team: TeamListItemView
  detailEnabled?: boolean
}

export const TeamListItem = ({ team, detailEnabled = false }: TeamListItemProps) => {
  return (
    <Link to={AbsolutePaths.TEAMS + '/details/' + team.id}>
      <div
        className={cn(
          'mt-5 flex items-center justify-between rounded-lg bg-secondary text-secondary-foreground p-4 transition-transform border',
          detailEnabled && 'hover:translate-x-2'
        )}
      >
        <div className="flex items-center gap-4 overflow-hidden">
          <div className="flex flex-col items-start gap-1 overflow-hidden">
            <div className="flex flex-col items-baseline gap-2 md:flex-row md:gap-4">
              <h3 className="max-w-full text-lg font-bold">{team.name}</h3>
              <div className="flex flex-wrap gap-2">
                {team.labels &&
                  team.labels.map((label, index) => (
                    <TeamLabel name={label.name} color={label.color} desc={label.description} key={index} />
                  ))}
              </div>
            </div>
            {team.introduction && <div className="text-muted-foreground">{team.introduction}</div>}
          </div>
        </div>

        <div className="flex items-center gap-4">
          {team.logo && <img src={team.logo} alt={team.name} className="h-16 w-16 rounded-md object-contain" loading="lazy" />}
          {detailEnabled && <ChevronRight className="h-10 w-10 text-muted-foreground/50 md:h-16 md:w-16" />}
        </div>
      </div>
    </Link>
  )
}
