import { CircularProgress } from '@/common-components/CircularProgress'
import { useOpaqueBackground } from '@/util/core-functions.util'
import type { TeamStatView } from '@/util/views/team.view'
import { Link } from 'react-router'

interface TeamStatProps {
  stat: TeamStatView
  className?: string
}

export function TeamStat({ stat, className }: TeamStatProps) {
  const { name, value1, value2, navigate, percentage } = stat
  const background = useOpaqueBackground(1)
  const backgroundHover = useOpaqueBackground(2)

  const content = (
    <div
      className={`rounded-lg px-5 py-2 transition-colors ${className}`}
      style={{ backgroundColor: background }}
      onMouseEnter={(e) => navigate && (e.currentTarget.style.backgroundColor = backgroundHover)}
      onMouseLeave={(e) => navigate && (e.currentTarget.style.backgroundColor = background)}
    >
      <div className="flex flex-row justify-between items-center">
        <div>
          <div className="text-sm font-medium text-muted-foreground">{name}</div>
          <div className="text-2xl font-bold">{value1}</div>
          <div className="text-xs text-muted-foreground">{value2}</div>
        </div>
        {typeof percentage !== 'undefined' && <CircularProgress value={percentage} size={48} strokeWidth={3} label="" />}
      </div>
    </div>
  )

  if (navigate) return <Link to={navigate}>{content}</Link>
  return content
}
