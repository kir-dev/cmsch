import { CmschPage } from '@/common-components/layout/CmschPage'
import { LinkButton } from '@/common-components/LinkButton'
import { Button } from '@/components/ui/button'
import { useBrandColor } from '@/util/core-functions.util.ts'
import { l } from '@/util/language'
import { useNavigate } from 'react-router'

export const UnauthorizedPage = () => {
  const navigate = useNavigate()
  const brandColor = useBrandColor()

  return (
    <CmschPage title={l('unauthorized-page-helmet')}>
      <h1 className="text-3xl font-bold font-heading text-center">{l('unauthorized-page-title')}</h1>
      <p className="text-center text-gray-500 mt-10">{l('unauthorized-page-description')}</p>
      <div className="flex justify-center mt-10 space-x-4">
        <Button style={{ backgroundColor: brandColor }} onClick={() => navigate('/login')}>
          Belépés
        </Button>
        <LinkButton href="/" variant="ghost">
          Főoldal
        </LinkButton>
      </div>
    </CmschPage>
  )
}
