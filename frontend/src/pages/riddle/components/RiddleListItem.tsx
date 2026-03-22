import type { Riddle } from '@/util/views/riddle.view.ts'

interface RiddleListItemProps {
  riddle: Riddle
  onClick: () => void
}

export function RiddleListItem({ riddle, onClick }: RiddleListItemProps) {
  return (
    <div
      key={riddle.id}
      className="cursor-pointer rounded-md bg-secondary text-secondary-foreground px-6 py-2 transition-colors hover:bg-secondary/80 border"
      onClick={onClick}
    >
      <span className="text-xl font-bold">{riddle.title}</span>
    </div>
  )
}
