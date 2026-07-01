import { useOpaqueBackground } from '@/util/core-functions.util.ts'
import type { DebtView } from '@/util/views/debt.view.ts'
import { CheckCircle2, User, XCircle } from 'lucide-react'

interface DebtListItemProps {
  item: DebtView
}

export const DebtListItem = ({ item }: DebtListItemProps) => {
  const bg = useOpaqueBackground(1)

  return (
    <div className="rounded-lg p-4 mt-5" style={{ backgroundColor: bg }}>
      <div className="flex flex-row space-x-4 justify-between items-center">
        <div className="truncate flex items-center gap-1">
          <User className="h-4 w-4" />
          {item.product}
        </div>
        <div>{item.price}&nbsp;JMF</div>
        {item.payed ? (
          <div className="text-success flex items-center gap-1 font-bold">
            <CheckCircle2 className="h-4 w-4" /> Fizetve
          </div>
        ) : (
          <div className="text-danger flex items-center gap-1 font-bold">
            <XCircle className="h-4 w-4" /> Fizetetlen
          </div>
        )}
      </div>
    </div>
  )
}
