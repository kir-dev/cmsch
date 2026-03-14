import { cn } from '@/lib/utils'
import { AbsolutePaths } from '@/util/paths'
import type { TaskCategoryPreview } from '@/util/views/task.view'
import { Link } from 'react-router'

export const TaskCategoryListItem = ({ category }: { category: TaskCategoryPreview }) => {
  const percentage = (category.sum > 0 ? (category.approved + category.notGraded) / category.sum : 0) * 100
  const radius = 18
  const circumference = 2 * Math.PI * radius
  const strokeDashoffset = circumference - (percentage / 100) * circumference

  return (
    <div className="rounded-md bg-secondary text-secondary-foreground px-6 py-2 transition-colors hover:bg-secondary/80 border">
      <Link to={`${AbsolutePaths.TASKS}/category/${category.categoryId}`}>
        <div className="flex items-center justify-between">
          <span className="text-xl font-bold">{category.name}</span>
          <div className="flex items-center gap-2">
            <span className="font-bold">
              {category.approved + category.notGraded}/{category.sum}
            </span>
            <div className="relative flex h-10 w-10 items-center justify-center">
              <svg className="h-full w-full -rotate-90 transform">
                <circle className="text-border" strokeWidth="4" stroke="currentColor" fill="transparent" r={radius} cx="20" cy="20" />
                <circle
                  className={cn(category.notGraded > 0 ? 'text-warning' : 'text-success')}
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
      </Link>
    </div>
  )
}
