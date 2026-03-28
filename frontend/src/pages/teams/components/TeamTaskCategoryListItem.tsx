import { CircularProgress } from '@/common-components/CircularProgress'
import { joinPath, useOpaqueBackground } from '@/util/core-functions.util'
import { AbsolutePaths } from '@/util/paths'
import type { TeamTaskCategoriesView } from '@/util/views/team.view'
import { Link } from 'react-router'

export const TeamTaskCategoryListItem = ({ category }: { category: TeamTaskCategoriesView }) => {
  const bg = useOpaqueBackground(1)
  const hoverBg = useOpaqueBackground(2)
  const percentage = (category.completed / (category.outOf || 1)) * 100

  const innerComponent = (
    <div className="flex items-center justify-between">
      <span className="font-bold text-xl">{category.name}</span>
      <div className="flex flex-row items-center space-x-2">
        <span className="font-bold">
          {category.completed}/{category.outOf}
        </span>
        <CircularProgress value={percentage} size={40} strokeWidth={3} label="" />
      </div>
    </div>
  )
  if (typeof category.navigate === 'undefined' || category.navigate === null)
    return (
      <div className="px-6 py-2 mt-5 rounded-md transition-colors" style={{ backgroundColor: bg }}>
        {innerComponent}
      </div>
    )
  return (
    <div
      className="px-6 py-2 mt-5 rounded-md transition-colors cursor-pointer group"
      style={{ backgroundColor: bg }}
      onMouseEnter={(e) => (e.currentTarget.style.backgroundColor = hoverBg)}
      onMouseLeave={(e) => (e.currentTarget.style.backgroundColor = bg)}
    >
      <Link to={joinPath(AbsolutePaths.TASKS, 'category', category.navigate)}>{innerComponent}</Link>
    </div>
  )
}
