import { useConfigContext } from '@/api/contexts/config/ConfigContext'
import { useAddSupportMessageMutation } from '@/api/hooks/support/useAddSupportMessageMutation'
import { useSupportThreadQuery } from '@/api/hooks/support/useSupportThreadQuery'
import { CmschPage } from '@/common-components/layout/CmschPage'
import { PageStatus } from '@/common-components/PageStatus'
import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import { Textarea } from '@/components/ui/textarea'
import { useToast } from '@/hooks/use-toast'
import { cn } from '@/lib/utils'
import { stringifyTimeStamp } from '@/util/core-functions.util'
import { AbsolutePaths } from '@/util/paths'
import { SupportThreadStatus } from '@/util/views/support.view'
import { useState } from 'react'
import { Link, useParams, useSearchParams } from 'react-router'

const SupportThreadPage = () => {
  const config = useConfigContext()
  const adminLabel = config?.components?.support?.adminLabel ?? 'rendező'

  const { uuid } = useParams<{ uuid: string }>()
  const [searchParams] = useSearchParams()
  const secret = searchParams.get('secret') ?? undefined
  const { toast } = useToast()
  const [replyText, setReplyText] = useState('')

  const { isLoading, isError, data, refetch } = useSupportThreadQuery(uuid ?? '', secret)
  const addMessageMutation = useAddSupportMessageMutation()

  if (!uuid) return null

  if (isLoading || isError || !data) {
    return <PageStatus isLoading={isLoading} isError={isError} title="Üzenetváltás" />
  }

  const { thread, messages, canReply } = data

  const statusLabel: Record<string, string> = {
    [SupportThreadStatus.WAITING_FOR_ADMIN]: `${adminLabel.charAt(0).toUpperCase() + adminLabel.slice(1)} válaszára vár`,
    [SupportThreadStatus.WAITING_FOR_CUSTOMER]: 'A te válaszodra vár',
    [SupportThreadStatus.DONE]: 'Lezárva'
  }

  const handleReply = () => {
    if (!replyText.trim()) return
    addMessageMutation.mutate(
      { uuid, content: replyText, secret },
      {
        onSuccess: () => {
          setReplyText('')
          refetch()
        },
        onError: () => {
          toast({ title: 'Hiba', description: 'Nem sikerült elküldeni a választ.', variant: 'destructive' })
        }
      }
    )
  }

  return (
    <CmschPage title={thread.title}>
      <div className="mb-2">
        <Button variant="ghost" size="sm" asChild className="-ml-2 mb-2">
          <Link to={AbsolutePaths.SUPPORT}>Vissza a listához</Link>
        </Button>
      </div>

      <div className="mb-6 flex items-start justify-between gap-4 flex-wrap">
        <div>
          <h1 className="text-3xl font-bold tracking-tight mb-1">{thread.title}</h1>
          <p className="text-sm text-muted-foreground">
            {thread.userName} · {stringifyTimeStamp(thread.createdAt)}
            {thread.solver && ` · Válaszol: ${thread.solver}`}
          </p>
        </div>
        <Badge
          variant={
            thread.status === SupportThreadStatus.DONE
              ? 'outline'
              : thread.status === SupportThreadStatus.WAITING_FOR_CUSTOMER
                ? 'default'
                : 'secondary'
          }
        >
          {statusLabel[thread.status]}
        </Badge>
      </div>

      <div className="flex flex-col gap-3 mb-6">
        {messages.length === 0 && <p className="text-muted-foreground text-sm">Még nincsenek üzenetek.</p>}
        {messages.map((msg) => (
          <div
            key={msg.id}
            className={cn(
              'rounded-lg border p-4',
              msg.fromAdmin ? 'bg-primary/5 border-primary/20' : 'bg-card'
            )}
          >
            <div className="flex items-center justify-between mb-2 gap-2 flex-wrap">
              <span className="font-medium text-sm">
                {msg.authorName}
                {msg.fromAdmin && <span className="ml-1 text-xs text-muted-foreground">({adminLabel})</span>}
              </span>
              <span className="text-xs text-muted-foreground">{stringifyTimeStamp(msg.createdAt)}</span>
            </div>
            <p className="whitespace-pre-wrap text-sm">{msg.content}</p>
          </div>
        ))}
      </div>

      {canReply && (
        <div className="flex flex-col gap-2 w-full">
          <Textarea
            rows={4}
            value={replyText}
            onChange={(e) => setReplyText(e.target.value)}
            placeholder="Írj választ..."
            className="w-full"
          />
          <Button
            onClick={handleReply}
            disabled={addMessageMutation.isPending || !replyText.trim()}
            className="self-start"
          >
            {addMessageMutation.isPending ? 'Küldés...' : 'Válasz küldése'}
          </Button>
        </div>
      )}

      {thread.status === SupportThreadStatus.DONE && (
        <p className="text-muted-foreground text-sm mt-2">Ez az üzenetváltás le van zárva.</p>
      )}
    </CmschPage>
  )
}

export default SupportThreadPage
