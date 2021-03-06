/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jface.viewers;

import org.eclipse.jface.util.Assert;
import org.eclipse.jface.util.ListenerList;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

/**
 * A decorating label provider is a label provider which combines 
 * a nested label provider and an optional decorator.
 * The decorator decorates the label text, image, font and colors provided by 
 * the nested label provider.
 */
public class DecoratingLabelProvider extends LabelProvider implements
        ILabelProvider, IViewerLabelProvider, IColorProvider, IFontProvider {
    private ILabelProvider provider;

    private ILabelDecorator decorator;

    // Need to keep our own list of listeners
    private ListenerList listeners = new ListenerList();

    /**
     * Creates a decorating label provider which uses the given label decorator
     * to decorate labels provided by the given label provider.
     *
     * @param provider the nested label provider
     * @param decorator the label decorator, or <code>null</code> if no decorator is to be used initially
     */
    public DecoratingLabelProvider(ILabelProvider provider,
            ILabelDecorator decorator) {
        Assert.isNotNull(provider);
        this.provider = provider;
        this.decorator = decorator;
    }

    /**
     * The <code>DecoratingLabelProvider</code> implementation of this <code>IBaseLabelProvider</code> method
     * adds the listener to both the nested label provider and the label decorator.
     *
     * @param listener a label provider listener
     */
    public void addListener(ILabelProviderListener listener) {
        super.addListener(listener);
        provider.addListener(listener);
        if (decorator != null) {
            decorator.addListener(listener);
        }
        listeners.add(listener);
    }

    /**
     * The <code>DecoratingLabelProvider</code> implementation of this <code>IBaseLabelProvider</code> method
     * disposes both the nested label provider and the label decorator.
     */
    public void dispose() {
        provider.dispose();
        if (decorator != null) {
            decorator.dispose();
        }
    }

    /**
     * The <code>DecoratingLabelProvider</code> implementation of this 
     * <code>ILabelProvider</code> method returns the image provided
     * by the nested label provider's <code>getImage</code> method, 
     * decorated with the decoration provided by the label decorator's
     * <code>decorateImage</code> method.
     */
    public Image getImage(Object element) {
        Image image = provider.getImage(element);
        if (decorator != null) {
            Image decorated = decorator.decorateImage(image, element);
            if (decorated != null) {
                return decorated;
            }
        }
        return image;
    }

    /**
     * Returns the label decorator, or <code>null</code> if none has been set.
     *
     * @return the label decorator, or <code>null</code> if none has been set.
     */
    public ILabelDecorator getLabelDecorator() {
        return decorator;
    }

    /**
     * Returns the nested label provider.
     *
     * @return the nested label provider
     */
    public ILabelProvider getLabelProvider() {
        return provider;
    }

    /**
     * The <code>DecoratingLabelProvider</code> implementation of this 
     * <code>ILabelProvider</code> method returns the text label provided
     * by the nested label provider's <code>getText</code> method, 
     * decorated with the decoration provided by the label decorator's
     * <code>decorateText</code> method.
     */
    public String getText(Object element) {
        String text = provider.getText(element);
        if (decorator != null) {
            String decorated = decorator.decorateText(text, element);
            if (decorated != null) {
                return decorated;
            }
        }
        return text;
    }

    /**
     * The <code>DecoratingLabelProvider</code> implementation of this 
     * <code>IBaseLabelProvider</code> method returns <code>true</code> if the corresponding method
     * on the nested label provider returns <code>true</code> or if the corresponding method on the 
     * decorator returns <code>true</code>.
     */
    public boolean isLabelProperty(Object element, String property) {
        if (provider.isLabelProperty(element, property))
            return true;
        if (decorator != null && decorator.isLabelProperty(element, property))
            return true;
        return false;
    }

    /**
     * The <code>DecoratingLabelProvider</code> implementation of this <code>IBaseLabelProvider</code> method
     * removes the listener from both the nested label provider and the label decorator.
     *
     * @param listener a label provider listener
     */
    public void removeListener(ILabelProviderListener listener) {
        super.removeListener(listener);
        provider.removeListener(listener);
        if (decorator != null) {
            decorator.removeListener(listener);
        }
        listeners.remove(listener);
    }

    /**
     * Sets the label decorator.
     * Removes all known listeners from the old decorator, and adds all known listeners to the new decorator.
     * The old decorator is not disposed.
     * Fires a label provider changed event indicating that all labels should be updated.
     * Has no effect if the given decorator is identical to the current one.
     *
     * @param decorator the label decorator, or <code>null</code> if no decorations are to be applied
     */
    public void setLabelDecorator(ILabelDecorator decorator) {
        ILabelDecorator oldDecorator = this.decorator;
        if (oldDecorator != decorator) {
            Object[] listenerList = this.listeners.getListeners();
            if (oldDecorator != null) {
                for (int i = 0; i < listenerList.length; ++i) {
                    oldDecorator
                            .removeListener((ILabelProviderListener) listenerList[i]);
                }
            }
            this.decorator = decorator;
            if (decorator != null) {
                for (int i = 0; i < listenerList.length; ++i) {
                    decorator
                            .addListener((ILabelProviderListener) listenerList[i]);
                }
            }
            fireLabelProviderChanged(new LabelProviderChangedEvent(this));
        }
    }


    /*
     *  (non-Javadoc)
     * @see org.eclipse.jface.viewers.IViewerLabelProvider#updateLabel(org.eclipse.jface.viewers.ViewerLabel, java.lang.Object)
     */
    public void updateLabel(ViewerLabel settings, Object element) {

        ILabelDecorator currentDecorator = getLabelDecorator();
        String oldText = settings.getText();
        boolean decorationReady = true;
        if (currentDecorator instanceof IDelayedLabelDecorator) {
            IDelayedLabelDecorator delayedDecorator = (IDelayedLabelDecorator) currentDecorator;
            if (!delayedDecorator.prepareDecoration(element, oldText)) {
                // The decoration is not ready but has been queued for processing
                decorationReady = false;
            }
        }
        // update icon and label

        if (decorationReady || oldText == null
                || settings.getText().length() == 0)
            settings.setText(getText(element));

        Image oldImage = settings.getImage();
        if (decorationReady || oldImage == null) {
            settings.setImage(getImage(element));
        }
 
        if(decorationReady)
        	updateForDecorationReady(settings,element);

    }

	/**
	 * Decoration is ready. Update anything else for the settings.
	 * @param settings The object collecting the settings.
	 * @param element The Object being decorated.
	 * @since 3.1
	 */
	protected void updateForDecorationReady(ViewerLabel settings, Object element) {
		
		if(decorator instanceof IColorDecorator){
			IColorDecorator colorDecorator = (IColorDecorator) decorator;
			settings.setBackground(colorDecorator.decorateBackground(element));
			settings.setForeground(colorDecorator.decorateForeground(element));
		}
		
		if(decorator instanceof IFontDecorator)
			settings.setFont(((IFontDecorator) decorator).decorateFont(element));
		
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IColorProvider#getBackground(java.lang.Object)
	 */
	public Color getBackground(Object element) {
		if(provider instanceof IColorProvider)
			return ((IColorProvider) provider).getBackground(element);
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IFontProvider#getFont(java.lang.Object)
	 */
	public Font getFont(Object element) {
		if(provider instanceof IFontProvider)
			return ((IFontProvider) provider).getFont(element);
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IColorProvider#getForeground(java.lang.Object)
	 */
	public Color getForeground(Object element) {
		if(provider instanceof IColorProvider)
			return ((IColorProvider) provider).getForeground(element);
		return null;
	}

}
