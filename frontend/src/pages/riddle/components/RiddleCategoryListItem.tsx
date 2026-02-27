import type { RiddleCategory } from '@/util/views/riddle.view'

interface RiddleCategoryListItemProps {
  category: RiddleCategory
  onClick: () => void
}

export function RiddleCategoryListItem({ category, onClick }: RiddleCategoryListItemProps) {
  const percentage = (category.completed / category.total) * 100
  const radius = 18
  const circumference = 2 * Math.PI * radius
  const strokeDashoffset = circumference - (percentage / 100) * circumference

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
          <div className="relative flex h-10 w-10 items-center justify-center">
            <svg className="h-full w-full -rotate-90 transform">
              <circle className="text-border" strokeWidth="4" stroke="currentColor" fill="transparent" r={radius} cx="20" cy="20" />
              <circle
                className="text-success"
                strokeWidth="4"
                strokeDasharray={circumference}
                style={{ strokeDashoffset }}
                strokeLinecap="round"
                stroke="currentColor"
                fill="transparent"
                r={radius}
                cx="20"
                cy="20"
              />
            </svg>
          </div>
        </div>
      </div>
    </div>
  )
}
