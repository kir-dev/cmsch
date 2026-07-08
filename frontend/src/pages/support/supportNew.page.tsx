import { useAuthContext } from '@/api/contexts/auth/useAuthContext'
import { useConfigContext } from '@/api/contexts/config/ConfigContext'
import { useCreateSupportThreadMutation } from '@/api/hooks/support/useCreateSupportThreadMutation'
import { CmschPage } from '@/common-components/layout/CmschPage'
import Markdown from '@/common-components/Markdown'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Textarea } from '@/components/ui/textarea'

import { useToast } from '@/hooks/use-toast'
import { AbsolutePaths } from '@/util/paths'
import axios from 'axios'
import { Link, useNavigate } from 'react-router'

const SupportNewPage = () => {
  const config = useConfigContext()
  const support = config?.components?.support
  const maxOpen = support?.maxOpenThreads ?? 5
  const buttonLabel = support?.newThreadButtonLabel ?? 'Új üzenet'
  const { isLoggedIn } = useAuthContext()
  const { toast } = useToast()
  const navigate = useNavigate()
  const mutation = useCreateSupportThreadMutation()

  const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault()
    const form = new FormData(e.currentTarget)
    const title = (form.get('title') as string).trim()
    const content = (form.get('content') as string).trim()
    const authorName = (form.get('authorName') as string | null)?.trim() ?? ''
    const authorEmail = (form.get('authorEmail') as string | null)?.trim() ?? ''

    if (!title || !content) {
      toast({ title: 'Hiba', description: 'A tárgy és az üzenet megadása kötelező.', variant: 'destructive' })
      return
    }
    if (!isLoggedIn && !authorEmail) {
      toast({ title: 'Hiba', description: 'Bejelentkezés nélkül az email cím megadása kötelező.', variant: 'destructive' })
      return
    }

    mutation.mutate(
      { title, content, authorName, authorEmail },
      {
        onSuccess: (data) => navigate(`${AbsolutePaths.SUPPORT}/${data.uuid}?secret=${data.uuidSecret}`, { replace: true }),
        onError: (err) => {
          if (axios.isAxiosError(err) && err.response?.status === 429) {
            toast({
              title: 'Túl sok nyitott megkeresés',
              description: `Egyszerre legfeljebb ${maxOpen} nyitott üzenetváltásod lehet.`,
              variant: 'destructive'
            })
          } else {
            toast({ title: 'Hiba', description: 'Nem sikerült elküldeni az üzenetet.', variant: 'destructive' })
          }
        }
      }
    )
  }

  return (
    <CmschPage title={buttonLabel}>
      <div className="mb-2">
        <Button variant="ghost" size="sm" asChild className="-ml-2 mb-2">
          <Link to={AbsolutePaths.SUPPORT}>Vissza</Link>
        </Button>
      </div>
      <h1 className="text-4xl font-bold tracking-tight mb-4">{buttonLabel}</h1>

      {support?.newThreadTopMessage && (
        <div className="mb-4">
          <Markdown text={support.newThreadTopMessage} />
        </div>
      )}

      {!isLoggedIn ? (
        <div className="rounded-lg border p-4 bg-muted text-sm text-muted-foreground">
          <p className="font-medium text-foreground mb-1">Bejelentkezés szükséges</p>
          <p>Új ügyfélszolgálati szálat csak bejelentkezés után nyithatsz. Email küldéssel is kapcsolatba léphetsz.</p>
        </div>
      ) : (
        <form onSubmit={handleSubmit} className="w-full mx-auto mt-5">
          <div className="mt-5">
            <Label htmlFor="title" className="text-xl">
              Tárgy *
            </Label>
            <div className="mt-2">
              <Input id="title" name="title" placeholder="Mi a kérdésed témája?" required />
            </div>
          </div>
          <div className="mt-5">
            <Label htmlFor="content" className="text-xl">
              Üzenet *
            </Label>
            <div className="mt-2">
              <Textarea id="content" name="content" rows={6} placeholder="Írd le részletesen a kérdésedet..." required />
            </div>
          </div>
          <div className="flex justify-end mt-10">
            <Button type="submit" disabled={mutation.isPending}>
              {mutation.isPending ? 'Küldés...' : 'Küldés'}
            </Button>
          </div>
        </form>
      )}

      {support?.newThreadBottomMessage && (
        <div className="mt-6">
          <Markdown text={support.newThreadBottomMessage} />
        </div>
      )}
    </CmschPage>
  )
}

export default SupportNewPage
