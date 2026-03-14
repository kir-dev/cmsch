import { formatHu, joinPath, useOpaqueBackground } from '@/util/core-functions.util'
import { AbsolutePaths } from '@/util/paths'
import type { TeamFormView } from '@/util/views/team.view'
import { AlertCircle, CheckCircle2 } from 'lucide-react'
import { Link } from 'react-router'

export function TeamFormItem({ form }: { form: TeamFormView }) {
  const bg = useOpaqueBackground(1)
  const hoverBg = useOpaqueBackground(2)
  return (
    <Link to={joinPath(AbsolutePaths.FORM, form.url)}>
      <div
        className="px-6 py-2 mt-5 rounded-md transition-colors group"
        style={{ backgroundColor: bg }}
        onMouseEnter={(e) => (e.currentTarget.style.backgroundColor = hoverBg)}
        onMouseLeave={(e) => (e.currentTarget.style.backgroundColor = bg)}
      >
        <div className="flex items-center justify-between">
          <div>
            <div className="font-bold text-xl">{form.name}</div>
            <p>{form.filled ? 'Kitöltve' : `Határidő: ${formatHu(new Date(form.availableUntil * 1000), 'MM. dd. HH:mm')}`}</p>
          </div>
          <div className={form.filled ? 'text-success' : 'text-danger'}>
            {form.filled ? <CheckCircle2 className="h-6 w-6" /> : <AlertCircle className="h-6 w-6" />}
          </div>
        </div>
      </div>
    </Link>
  )
}
