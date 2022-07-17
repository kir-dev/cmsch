import { Helmet } from 'react-helmet'
import { useConfigContext } from './api/contexts/config/ConfigContext'
import { API_BASE_URL } from './util/configs/environment.config'
import { useColorModeValue } from '@chakra-ui/system'

export const MetaTags = () => {
  const config = useConfigContext()
  const style = config?.components.style
  return (
    <Helmet>
      <meta charSet="utf-8" />
      <meta name="viewport" content="width=device-width, initial-scale=1.0" />
      <link rel="icon" type="image/png" sizes="192x192" href={`${API_BASE_URL}/cdn/manifest/icon-192x192.png`} />
      <link rel="icon" type="image/png" sizes="256x256" href={`${API_BASE_URL}/cdn/manifest/icon-256x256.png`} />
      <link rel="icon" type="image/png" sizes="384x384" href={`${API_BASE_URL}/cdn/manifest/icon-384x384.png`} />
      <link rel="icon" type="image/png" sizes="512x512" href={`${API_BASE_URL}/cdn/manifest/icon-512x512.png`} />
      <link rel="icon" type="image/x-icon" href={`${API_BASE_URL}/cdn/manifest/favicon.ico`} />
      <link rel="manifest" href={`${API_BASE_URL}/manifest/manifest.json`} />
      <meta name="msapplication-TileColor" content={useColorModeValue(style?.lightBrandingColor, style?.darkBrandingColor)} />
      <meta name="msapplication-TileImage" content="/img/icons/ms-icon-144x144.png" />
      <meta name="theme-color" content="#ffffff" />
    </Helmet>
  )
}
