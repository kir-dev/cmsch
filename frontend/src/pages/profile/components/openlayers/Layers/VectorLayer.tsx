import { useContext, useEffect, useState } from 'react'
import MapContext from '../MapContext'
import OLVectorLayer from 'ol/layer/Vector'
import OLVectorSource from 'ol/source/Vector'
import Style from 'ol/style/Style'
import { Geometry } from 'ol/geom'

type VectorLayerProps = {
  source: OLVectorSource
  style?: Style
  zIndex?: number
}

const VectorLayer = ({ source, style, zIndex = 0 }: VectorLayerProps) => {
  const map = useContext(MapContext)
  const [layer, setLayer] = useState<OLVectorLayer<OLVectorSource<Geometry>>>()

  useEffect(() => {
    if (!map) return
    if (layer) {
      map.removeLayer(layer)
    }
    let vectorLayer = new OLVectorLayer({
      source,
      style
    })
    setLayer(vectorLayer)
    map.addLayer(vectorLayer)
    vectorLayer.setZIndex(zIndex)
    return () => {
      if (map && layer) {
        map.removeLayer(layer)
      }
    }
  }, [map, source])

  return null
}
export default VectorLayer
