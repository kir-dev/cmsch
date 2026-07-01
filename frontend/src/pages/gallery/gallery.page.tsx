import { useConfigContext } from '@/api/contexts/config/ConfigContext'
import { useGalleryQuery } from '@/api/hooks/gallery/useGalleryQuery'
import { ComponentUnavailable } from '@/common-components/ComponentUnavailable'
import { CmschPage } from '@/common-components/layout/CmschPage'
import Markdown from '@/common-components/Markdown.tsx'
import { PageStatus } from '@/common-components/PageStatus'
import GalleryMasonry from './components/GalleryMasonry'

const GalleryPage = () => {
  const { data, isLoading, isError } = useGalleryQuery()
  const component = useConfigContext()?.components?.gallery

  if (!component) return <ComponentUnavailable />

  if (isError || isLoading || !data) return <PageStatus isLoading={isLoading} isError={isError} title={component?.title} />

  return (
    <CmschPage title={component?.title}>
      <h1 className="text-3xl font-bold font-heading my-5">{component.title || 'Galéria'}</h1>
      <Markdown text={component.topMessage} />
      <GalleryMasonry photos={data.photos} />
      <Markdown text={component.bottomMessage} />
    </CmschPage>
  )
}

export default GalleryPage
