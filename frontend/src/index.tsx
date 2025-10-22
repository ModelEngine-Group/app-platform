/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { Suspense } from 'react';
import ReactDOM, { createRoot } from 'react-dom/client';
import singleSpaReact from 'single-spa-react';
import App from './app';

const render = (Component) => {
    const container = document.getElementById('mfe-content-right');
    const root = createRoot(container);
    return root.render(
        <Suspense fallback={<div id='Loading'>
            <div className='loader-inner ball-beat'>
                <div />
                <div />
                <div />
                <div />
                <div />
            </div>
        </div>}>
            <Component />
        </Suspense>
    );
}
if (!window.singleSpaNavigate) {
    render(App);
}

const reactLifecycles = singleSpaReact({
    renderType: 'createRoot',
    React,
    ReactDOM,
    rootComponent: App,
    domElementGetter: () => document.getElementById('mfe-content-right'),
    errorBoundary(err, info, props) {
        return <div>This renders when a catastrophic error occurs</div>;
    },
});
export const bootstrap = async (props) => {
    Promise.resolve(reactLifecycles.bootstrap(props));
}
export const mount = async (props) => {
    Promise.resolve(reactLifecycles.mount(props))
};

export const unmount = async (props) => {
    Promise.resolve(reactLifecycles.unmount(props))
};