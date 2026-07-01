import { CircularProgress } from '@/common-components/CircularProgress'
import type { RiddleCategory } from '@/util/views/riddle.view'

interface RiddleCategoryListItemProps {
  category: RiddleCategory
  onClick: () => void
}

export function RiddleCategoryListItem({ category, onClick }: RiddleCategoryListItemProps) {
  const percentage = (category.completed / category.total) * 100

  return (
    <div
      key={category.categoryId}
      className="cursor-pointer rounded-md bg-secondary text-secondary-foreground px-6 py-2 transition-colors hover:bg-secondary/80 border"
      onClick={onClick}
    >
      <div className="flex items-center justify-between">
        <span className="text-xl font-bold">{category.title}</span>
        <div className="flex items-center gap-2">
          <span className="font-bold">
            {category.completed}/{category.total}
          </span>
          <CircularProgress value={percentage} size={40} strokeWidth={4} label="" />
        </div>
      </div>
    </div>
  )
}
